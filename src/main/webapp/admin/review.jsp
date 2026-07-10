<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>评价管理 - 房屋租赁系统管理端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            display: flex;
            min-height: 100vh;
            background-color: #f0f2f5;
            font-family: "Microsoft YaHei", sans-serif;
        }

        .main-content {
            flex: 1;
            padding: 24px;
            margin-left: 220px;
        }

        .page-header {
            margin-bottom: 24px;
        }

        .page-header h1 {
            font-size: 24px;
            color: #333;
            margin: 0 0 8px 0;
        }

        .page-header p {
            color: #666;
            margin: 0;
            font-size: 14px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-danger {
            background: #ff4d4f;
            color: #fff;
        }

        .btn-danger:hover {
            background: #ff7875;
        }

        .btn-small {
            padding: 4px 10px;
            font-size: 12px;
        }

        .table-container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        table th, table td {
            padding: 12px 16px;
            text-align: left;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
        }

        table th {
            background: #fafafa;
            color: #333;
            font-weight: 600;
        }

        table tr:hover {
            background: #f5f5f5;
        }

        .rating-stars {
            color: #faad14;
            font-size: 14px;
            letter-spacing: 2px;
        }

        .review-content {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .pagination {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            padding: 16px 20px;
            gap: 8px;
        }

        .pagination button {
            padding: 6px 12px;
            border: 1px solid #d9d9d9;
            background: #fff;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        .pagination button:hover {
            border-color: #1890ff;
            color: #1890ff;
        }

        .pagination button:disabled {
            color: #d9d9d9;
            cursor: not-allowed;
            border-color: #d9d9d9;
        }

        .pagination .page-info {
            font-size: 13px;
            color: #666;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>评价管理</h1>
            <p>查看和管理租户对房源的评价</p>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>租户</th>
                        <th>房源</th>
                        <th>评分</th>
                        <th>评价内容</th>
                        <th>评价日期</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="tableBody">
                    <tr>
                        <td colspan="7" style="text-align:center;color:#999;padding:40px;">加载中...</td>
                    </tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        var currentPage = 1;
        var pageSize = 10;

        /**
         * 页面初始化
         */
        document.addEventListener('DOMContentLoaded', function() {
            loadData(1);
        });

        /**
         * 加载评价列表数据
         * @param {number} page 页码
         */
        function loadData(page) {
            currentPage = page;
            var token = localStorage.getItem('token');

            var url = '/api/admin/review?page=' + page + '&pageSize=' + pageSize;

            fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    renderTable(result.data.list || []);
                    renderPagination(result.data.total || 0);
                } else {
                    alert('获取数据失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载数据失败：', error);
                document.getElementById('tableBody').innerHTML =
                    '<tr><td colspan="7" style="text-align:center;color:#ff4d4f;padding:40px;">加载失败，请稍后重试</td></tr>';
            });
        }

        /**
         * 渲染评分星星
         * @param {number} rating 评分（1-5）
         * @returns {string} 星星HTML
         */
        function renderStars(rating) {
            var stars = '';
            for (var i = 0; i < 5; i++) {
                stars += i < rating ? '★' : '☆';
            }
            return '<span class="rating-stars">' + stars + '</span>';
        }

        /**
         * 渲染表格数据
         * @param {Array} list 评价列表
         */
        function renderTable(list) {
            var tbody = document.getElementById('tableBody');
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:40px;">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                html += '<tr>';
                html += '<td>' + (item.id || '') + '</td>';
                html += '<td>' + (item.tenantName || '') + '</td>';
                html += '<td>' + (item.houseAddress || '') + '</td>';
                html += '<td>' + renderStars(item.rating || 0) + '</td>';
                html += '<td><div class="review-content" title="' + (item.content || '') + '">' + (item.content || '') + '</div></td>';
                html += '<td>' + (item.createTime || '') + '</td>';
                html += '<td>';
                html += '<button class="btn btn-danger btn-small" onclick="deleteReview(' + item.id + ')">删除</button>';
                html += '</td>';
                html += '</tr>';
            });
            tbody.innerHTML = html;
        }

        /**
         * 渲染分页控件
         * @param {number} total 总记录数
         */
        function renderPagination(total) {
            var container = document.getElementById('pagination');
            var totalPages = Math.ceil(total / pageSize);

            if (totalPages <= 1) {
                container.innerHTML = '<span class="page-info">共 ' + total + ' 条记录</span>';
                return;
            }

            var html = '';
            html += '<button ' + (currentPage <= 1 ? 'disabled' : '') + ' onclick="loadData(' + (currentPage - 1) + ')">上一页</button>';
            html += '<span class="page-info">第 ' + currentPage + ' / ' + totalPages + ' 页，共 ' + total + ' 条</span>';
            html += '<button ' + (currentPage >= totalPages ? 'disabled' : '') + ' onclick="loadData(' + (currentPage + 1) + ')">下一页</button>';
            container.innerHTML = html;
        }

        /**
         * 删除评价
         * @param {number} id 评价ID
         */
        function deleteReview(id) {
            if (!confirm('确定要删除该评价吗？此操作不可撤销。')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch('/api/admin/review/' + id, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(result) {
                if (result.code === 200) {
                    loadData(currentPage);
                } else {
                    alert('删除失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('删除失败：', error);
                alert('删除失败，请稍后重试');
            });
        }
    </script>
</body>
</html>
