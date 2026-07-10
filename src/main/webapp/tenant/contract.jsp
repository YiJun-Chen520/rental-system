<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的合同 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>我的合同</h2>
            <div class="filter-bar">
                <select id="statusFilter" onchange="filterByStatus()">
                    <option value="">全部状态</option>
                    <option value="生效中">生效中</option>
                    <option value="已到期">已到期</option>
                    <option value="已终止">已终止</option>
                </select>
            </div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>房源地址</th>
                        <th>签订日期</th>
                        <th>起始日期</th>
                        <th>终止日期</th>
                        <th>月租金</th>
                        <th>押金</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody id="contractTableBody">
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
            loadContracts();
        });

        function loadContracts() {
            var token = localStorage.getItem('token');
            var status = document.getElementById('statusFilter').value;

            var url = '${pageContext.request.contextPath}/api/tenant/contract?page=' + currentPage + '&pageSize=' + pageSize;
            if (status) url += '&status=' + encodeURIComponent(status);

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
                    alert(result.message || '获取合同列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载合同列表失败:', error);
            });
        }

        function renderTable(contracts) {
            var tbody = document.getElementById('contractTableBody');
            if (contracts.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8">暂无数据</td></tr>';
                return;
            }
            var html = '';
            for (var i = 0; i < contracts.length; i++) {
                var c = contracts[i];
                html += '<tr>';
                html += '<td>' + c.id + '</td>';
                html += '<td>' + (c.houseAddress || '') + '</td>';
                html += '<td>' + (c.signDate || '') + '</td>';
                html += '<td>' + (c.startDate || '') + '</td>';
                html += '<td>' + (c.endDate || '') + '</td>';
                html += '<td>¥' + (c.rent || 0) + '</td>';
                html += '<td>¥' + (c.deposit || 0) + '</td>';
                html += '<td><span class="status-tag status-' + getStatusClass(c.status) + '">' + (c.status || '') + '</span></td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }

        function getStatusClass(status) {
            if (status === '生效中') return 'active';
            if (status === '已到期') return 'expired';
            if (status === '已终止') return 'terminated';
            return 'default';
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
            loadContracts();
        }

        function filterByStatus() {
            currentPage = 1;
            loadContracts();
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
