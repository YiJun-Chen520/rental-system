<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的评价 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>我的评价</h2>
            <div class="action-bar">
                <button onclick="showAddModal()">发表评价</button>
            </div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>房源地址</th>
                        <th>评分</th>
                        <th>评价内容</th>
                        <th>评价日期</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="reviewTableBody">
                    <tr>
                        <td colspan="6">加载中...</td>
                    </tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </main>
    </div>

    <!-- 添加/编辑评价弹窗 -->
    <div class="modal" id="reviewModal" style="display:none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modalTitle">发表评价</h3>
                <span class="modal-close" onclick="closeModal()">&times;</span>
            </div>
            <div class="modal-body">
                <input type="hidden" id="reviewId">
                <div class="form-group">
                    <label>房源ID：</label>
                    <input type="number" id="houseId" placeholder="请输入房源ID">
                </div>
                <div class="form-group">
                    <label>评分：</label>
                    <select id="rating">
                        <option value="1">1 - 很差</option>
                        <option value="2">2 - 较差</option>
                        <option value="3" selected>3 - 一般</option>
                        <option value="4">4 - 较好</option>
                        <option value="5">5 - 很好</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>评价内容：</label>
                    <textarea id="reviewContent" rows="4" placeholder="请输入评价内容"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button onclick="saveReview()">保存</button>
                <button onclick="closeModal()">取消</button>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        var currentPage = 1;
        var pageSize = 10;

        document.addEventListener('DOMContentLoaded', function() {
            loadReviews();
        });

        function loadReviews() {
            var token = localStorage.getItem('token');
            fetch('/api/tenant/review?page=' + currentPage + '&pageSize=' + pageSize, {
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
                    alert(result.message || '获取评价列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载评价列表失败:', error);
            });
        }

        function renderTable(reviews) {
            var tbody = document.getElementById('reviewTableBody');
            if (reviews.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6">暂无数据</td></tr>';
                return;
            }
            var html = '';
            for (var i = 0; i < reviews.length; i++) {
                var r = reviews[i];
                html += '<tr>';
                html += '<td>' + r.id + '</td>';
                html += '<td>' + (r.houseAddress || '') + '</td>';
                html += '<td>' + getStars(r.rating) + '</td>';
                html += '<td>' + (r.content || '') + '</td>';
                html += '<td>' + (r.reviewDate || '') + '</td>';
                html += '<td>';
                html += '<button class="btn-edit" onclick="showEditModal(' + r.id + ', ' + r.houseId + ', ' + r.rating + ', \'' + escapeStr(r.content) + '\')">编辑</button> ';
                html += '<button class="btn-delete" onclick="deleteReview(' + r.id + ')">删除</button>';
                html += '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }

        function escapeStr(str) {
            if (!str) return '';
            return str.replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/\n/g, '\\n');
        }

        function getStars(rating) {
            var stars = '';
            for (var i = 0; i < 5; i++) {
                stars += i < rating ? '★' : '☆';
            }
            return stars;
        }

        function showAddModal() {
            document.getElementById('modalTitle').textContent = '发表评价';
            document.getElementById('reviewId').value = '';
            document.getElementById('houseId').value = '';
            document.getElementById('houseId').disabled = false;
            document.getElementById('rating').value = '3';
            document.getElementById('reviewContent').value = '';
            document.getElementById('reviewModal').style.display = 'flex';
        }

        function showEditModal(id, houseId, rating, content) {
            document.getElementById('modalTitle').textContent = '编辑评价';
            document.getElementById('reviewId').value = id;
            document.getElementById('houseId').value = houseId;
            document.getElementById('houseId').disabled = true;
            document.getElementById('rating').value = rating;
            document.getElementById('reviewContent').value = content;
            document.getElementById('reviewModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('reviewModal').style.display = 'none';
        }

        function saveReview() {
            var id = document.getElementById('reviewId').value;
            var houseId = document.getElementById('houseId').value;
            var rating = document.getElementById('rating').value;
            var content = document.getElementById('reviewContent').value;

            if (!houseId) {
                alert('请输入房源ID');
                return;
            }
            if (!content) {
                alert('请输入评价内容');
                return;
            }

            var token = localStorage.getItem('token');
            var data = {
                houseId: parseInt(houseId),
                rating: parseInt(rating),
                content: content
            };

            var url = '/api/tenant/review';
            var method = 'POST';

            if (id) {
                data.id = parseInt(id);
                method = 'PUT';
            }

            fetch(url, {
                method: method,
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert(id ? '评价更新成功！' : '评价发表成功！');
                    closeModal();
                    loadReviews();
                } else {
                    alert(result.message || '操作失败');
                }
            })
            .catch(function(error) {
                console.error('保存评价失败:', error);
                alert('操作失败，请稍后重试');
            });
        }

        function deleteReview(id) {
            if (!confirm('确定删除该评价？')) return;

            var token = localStorage.getItem('token');
            fetch('/api/tenant/review?id=' + id, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('删除成功！');
                    loadReviews();
                } else {
                    alert(result.message || '删除失败');
                }
            })
            .catch(function(error) {
                console.error('删除评价失败:', error);
                alert('删除失败，请稍后重试');
            });
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
            loadReviews();
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
