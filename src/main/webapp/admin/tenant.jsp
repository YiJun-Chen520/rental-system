<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>租户管理 - 房屋租赁系统管理端</title>
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

        .toolbar {
            background: #fff;
            border-radius: 8px;
            padding: 16px 20px;
            margin-bottom: 16px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .toolbar input[type="text"] {
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            width: 240px;
            outline: none;
        }

        .toolbar input[type="text"]:focus {
            border-color: #1890ff;
            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-primary {
            background: #1890ff;
            color: #fff;
        }

        .btn-primary:hover {
            background: #40a9ff;
        }

        .btn-danger {
            background: #ff4d4f;
            color: #fff;
        }

        .btn-danger:hover {
            background: #ff7875;
        }

        .btn-success {
            background: #52c41a;
            color: #fff;
        }

        .btn-success:hover {
            background: #73d13d;
        }

        .btn-warning {
            background: #faad14;
            color: #fff;
        }

        .btn-warning:hover {
            background: #ffc53d;
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

        .status-enabled {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #f6ffed;
            color: #52c41a;
            font-size: 12px;
        }

        .status-disabled {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #fff1f0;
            color: #ff4d4f;
            font-size: 12px;
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

        .action-btns {
            display: flex;
            gap: 6px;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>租户管理</h1>
            <p>查看和管理所有租户信息</p>
        </div>

        <div class="toolbar">
            <input type="text" id="keyword" placeholder="输入姓名或手机号搜索" onkeydown="if(event.key==='Enter')loadData(1)">
            <button class="btn btn-primary" onclick="loadData(1)">搜索</button>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>姓名</th>
                        <th>手机号</th>
                        <th>身份证号</th>
                        <th>工作单位</th>
                        <th>状态</th>
                        <th>注册时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="tableBody">
                    <tr>
                        <td colspan="8" style="text-align:center;color:#999;padding:40px;">加载中...</td>
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
         * 加载租户列表数据
         * @param {number} page 页码
         */
        function loadData(page) {
            currentPage = page;
            var keyword = document.getElementById('keyword').value.trim();
            var token = localStorage.getItem('token');

            var url = getContextPath() + '/api/admin/tenant?page=' + page + '&pageSize=' + pageSize;
            if (keyword) {
                url += '&keyword=' + encodeURIComponent(keyword);
            }

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
                    '<tr><td colspan="8" style="text-align:center;color:#ff4d4f;padding:40px;">加载失败，请稍后重试</td></tr>';
            });
        }

        /**
         * 渲染表格数据
         * @param {Array} list 租户列表
         */
        function renderTable(list) {
            var tbody = document.getElementById('tableBody');
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:#999;padding:40px;">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                var statusClass = item.status === 1 ? 'status-enabled' : 'status-disabled';
                var statusText = item.status === 1 ? '启用' : '禁用';
                var toggleText = item.status === 1 ? '禁用' : '启用';
                var toggleClass = item.status === 1 ? 'btn-warning' : 'btn-success';

                html += '<tr>';
                html += '<td>' + (item.id || '') + '</td>';
                html += '<td>' + (item.name || '') + '</td>';
                html += '<td>' + (item.phone || '') + '</td>';
                html += '<td>' + (item.idCard || '') + '</td>';
                html += '<td>' + (item.workUnit || '') + '</td>';
                html += '<td><span class="' + statusClass + '">' + statusText + '</span></td>';
                html += '<td>' + (item.createTime || '') + '</td>';
                html += '<td class="action-btns">';
                html += '<button class="btn ' + toggleClass + ' btn-small" onclick="toggleStatus(' + item.id + ',' + (item.status === 1 ? 0 : 1) + ')">' + toggleText + '</button>';
                html += '<button class="btn btn-danger btn-small" onclick="deleteTenant(' + item.id + ')">删除</button>';
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
         * 切换租户状态
         * @param {number} id 租户ID
         * @param {number} status 目标状态
         */
        function toggleStatus(id, status) {
            var statusText = status === 1 ? '启用' : '禁用';
            if (!confirm('确定要' + statusText + '该租户吗？')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/tenant/' + id + '/status', {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status: status })
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(result) {
                if (result.code === 200) {
                    loadData(currentPage);
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('操作失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 删除租户
         * @param {number} id 租户ID
         */
        function deleteTenant(id) {
            if (!confirm('确定要删除该租户吗？此操作不可撤销。')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/tenant/' + id, {
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
