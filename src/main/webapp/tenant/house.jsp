<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>浏览房源 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>浏览房源</h2>
            <div class="search-bar">
                <input type="text" id="keyword" placeholder="输入关键词搜索">
                <input type="number" id="minRent" placeholder="最低租金">
                <input type="number" id="maxRent" placeholder="最高租金">
                <button onclick="searchHouses()">搜索</button>
            </div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>地址</th>
                        <th>户型</th>
                        <th>面积</th>
                        <th>月租金</th>
                        <th>配套设施</th>
                        <th>房东</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="houseTableBody">
                    <tr>
                        <td colspan="8">加载中...</td>
                    </tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        var currentPage = 1;
        var pageSize = 10;

        document.addEventListener('DOMContentLoaded', function() {
            loadHouses();
        });

        function loadHouses() {
            var token = localStorage.getItem('token');
            var keyword = document.getElementById('keyword').value;
            var minRent = document.getElementById('minRent').value;
            var maxRent = document.getElementById('maxRent').value;

            var url = '/api/tenant/house?page=' + currentPage + '&pageSize=' + pageSize;
            if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
            if (minRent) url += '&minRent=' + minRent;
            if (maxRent) url += '&maxRent=' + maxRent;

            fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    renderTable(result.data.list || []);
                    renderPagination(result.data.total || 0);
                } else {
                    alert(result.message || '获取房源列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载房源列表失败:', error);
            });
        }

        function renderTable(houses) {
            var tbody = document.getElementById('houseTableBody');
            if (houses.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8">暂无数据</td></tr>';
                return;
            }
            var html = '';
            for (var i = 0; i < houses.length; i++) {
                var h = houses[i];
                html += '<tr>';
                html += '<td>' + h.id + '</td>';
                html += '<td>' + (h.address || '') + '</td>';
                html += '<td>' + (h.houseType || '') + '</td>';
                html += '<td>' + (h.area || '') + '㎡</td>';
                html += '<td>¥' + (h.rent || 0) + '</td>';
                html += '<td>' + (h.facilities || '') + '</td>';
                html += '<td>' + (h.ownerName || '') + '</td>';
                html += '<td><a href="house-detail.jsp?id=' + h.id + '" class="btn-link">查看详情</a></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }

        function renderPagination(total) {
            var totalPages = Math.ceil(total / pageSize);
            var html = '';
            for (var i = 1; i <= totalPages; i++) {
                if (i === currentPage) {
                    html += '<span class="page-btn active">' + i + '</span>';
                } else {
                    html += '<span class="page-btn" onclick="goToPage(' + i + ')">' + i + '</span>';
                }
            }
            document.getElementById('pagination').innerHTML = html;
        }

        function goToPage(page) {
            currentPage = page;
            loadHouses();
        }

        function searchHouses() {
            currentPage = 1;
            loadHouses();
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
