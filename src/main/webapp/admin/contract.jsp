<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>合同管理 - 房屋租赁系统管理端</title>
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

        .toolbar select {
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            width: 140px;
            outline: none;
        }

        .toolbar select:focus {
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

        .status-active {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #f6ffed;
            color: #52c41a;
            font-size: 12px;
        }

        .status-expired {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #f0f0f0;
            color: #999;
            font-size: 12px;
        }

        .status-terminated {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #fff1f0;
            color: #ff4d4f;
            font-size: 12px;
        }

        .status-pending {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #e6f7ff;
            color: #1890ff;
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
            <h1>合同管理</h1>
            <p>查看和管理所有租赁合同</p>
        </div>

        <div class="toolbar">
            <select id="statusFilter">
                <option value="">全部状态</option>
                <option value="0">待生效</option>
                <option value="1">生效中</option>
                <option value="2">已到期</option>
                <option value="3">已终止</option>
            </select>
            <button class="btn btn-primary" onclick="loadData(1)">筛选</button>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>房源地址</th>
                        <th>租户</th>
                        <th>签订日期</th>
                        <th>起始日期</th>
                        <th>终止日期</th>
                        <th>月租金</th>
                        <th>押金</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="tableBody">
                    <tr>
                        <td colspan="10" style="text-align:center;color:#999;padding:40px;">加载中...</td>
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
         * 加载合同列表数据
         * @param {number} page 页码
         */
        function loadData(page) {
            currentPage = page;
            var status = document.getElementById('statusFilter').value;
            var token = localStorage.getItem('token');

            var url = getContextPath() + '/api/admin/contract?page=' + page + '&pageSize=' + pageSize;
            if (status !== '') {
                url += '&status=' + status;
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
                    '<tr><td colspan="10" style="text-align:center;color:#ff4d4f;padding:40px;">加载失败，请稍后重试</td></tr>';
            });
        }

        /**
         * 渲染表格数据
         * @param {Array} list 合同列表
         */
        function renderTable(list) {
            var tbody = document.getElementById('tableBody');
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;color:#999;padding:40px;">暂无数据</td></tr>';
                return;
            }

            var statusMap = {
                0: { cls: 'status-pending', text: '待生效' },
                1: { cls: 'status-active', text: '生效中' },
                2: { cls: 'status-expired', text: '已到期' },
                3: { cls: 'status-terminated', text: '已终止' }
            };

            var html = '';
            list.forEach(function(item) {
                var st = statusMap[item.status] || { cls: '', text: '未知' };

                html += '<tr>';
                html += '<td>' + (item.id || '') + '</td>';
                html += '<td>' + (item.houseAddress || '') + '</td>';
                html += '<td>' + (item.tenantName || '') + '</td>';
                html += '<td>' + (item.signDate || '') + '</td>';
                html += '<td>' + (item.startDate || '') + '</td>';
                html += '<td>' + (item.endDate || '') + '</td>';
                html += '<td>' + (item.rent ? '¥' + item.rent : '') + '</td>';
                html += '<td>' + (item.deposit ? '¥' + item.deposit : '') + '</td>';
                html += '<td><span class="' + st.cls + '">' + st.text + '</span></td>';
                html += '<td class="action-btns">';
                if (item.status === 1) {
                    html += '<button class="btn btn-warning btn-small" onclick="terminateContract(' + item.id + ')">终止</button>';
                    html += '<button class="btn btn-danger btn-small" onclick="expireContract(' + item.id + ')">到期</button>';
                }
                if (item.status === 0) {
                    html += '<button class="btn btn-warning btn-small" onclick="terminateContract(' + item.id + ')">终止</button>';
                }
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
         * 终止合同
         * @param {number} id 合同ID
         */
        function terminateContract(id) {
            if (!confirm('确定要终止该合同吗？此操作不可撤销。')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/contract/' + id + '/terminate', {
                method: 'PUT',
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
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('操作失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 将合同标记为到期
         * @param {number} id 合同ID
         */
        function expireContract(id) {
            if (!confirm('确定要将该合同标记为到期吗？')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/contract/' + id + '/expire', {
                method: 'PUT',
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
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('操作失败：', error);
                alert('操作失败，请稍后重试');
            });
        }
    </script>
</body>
</html>
