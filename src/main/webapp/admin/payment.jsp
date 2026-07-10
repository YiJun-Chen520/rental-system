<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>费用管理 - 房屋租赁系统管理端</title>
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
            flex-wrap: wrap;
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

        .btn-success {
            background: #52c41a;
            color: #fff;
        }

        .btn-success:hover {
            background: #73d13d;
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

        .status-paid {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            background: #f6ffed;
            color: #52c41a;
            font-size: 12px;
        }

        .status-unpaid {
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

        /* 模态框样式 */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        .modal {
            background: #fff;
            border-radius: 8px;
            padding: 24px;
            width: 420px;
            max-width: 90%;
            box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        }

        .modal h3 {
            margin: 0 0 20px 0;
            font-size: 18px;
            color: #333;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            color: #333;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
            box-sizing: border-box;
        }

        .form-group input:focus,
        .form-group select:focus {
            border-color: #1890ff;
            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
        }

        .modal-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 24px;
        }

        .btn-cancel {
            background: #f0f0f0;
            color: #666;
        }

        .btn-cancel:hover {
            background: #d9d9d9;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>费用管理</h1>
            <p>查看和管理所有费用记录</p>
        </div>

        <div class="toolbar">
            <select id="payStatusFilter">
                <option value="">全部缴费状态</option>
                <option value="0">未缴</option>
                <option value="1">已缴</option>
            </select>
            <select id="paymentTypeFilter">
                <option value="">全部费用类型</option>
                <option value="租金">租金</option>
                <option value="押金">押金</option>
                <option value="水电费">水电费</option>
                <option value="物业费">物业费</option>
            </select>
            <button class="btn btn-primary" onclick="loadData(1)">筛选</button>
            <button class="btn btn-success" onclick="openAddModal()">新增费用</button>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>合同编号</th>
                        <th>费用类型</th>
                        <th>金额</th>
                        <th>缴费状态</th>
                        <th>缴费日期</th>
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

    <!-- 新增费用模态框 -->
    <div class="modal-overlay" id="addModal">
        <div class="modal">
            <h3>新增费用</h3>
            <div class="form-group">
                <label>合同编号</label>
                <input type="number" id="addContractId" placeholder="请输入合同编号">
            </div>
            <div class="form-group">
                <label>费用类型</label>
                <select id="addPaymentType">
                    <option value="租金">租金</option>
                    <option value="押金">押金</option>
                    <option value="水电费">水电费</option>
                    <option value="物业费">物业费</option>
                </select>
            </div>
            <div class="form-group">
                <label>金额</label>
                <input type="number" id="addAmount" placeholder="请输入金额" step="0.01">
            </div>
            <div class="modal-actions">
                <button class="btn btn-cancel" onclick="closeAddModal()">取消</button>
                <button class="btn btn-primary" onclick="submitAdd()">确认添加</button>
            </div>
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
         * 加载费用列表数据
         * @param {number} page 页码
         */
        function loadData(page) {
            currentPage = page;
            var payStatus = document.getElementById('payStatusFilter').value;
            var paymentType = document.getElementById('paymentTypeFilter').value;
            var token = localStorage.getItem('token');

            var url = getContextPath() + '/api/admin/payment?page=' + page + '&pageSize=' + pageSize;
            if (payStatus !== '') {
                url += '&payStatus=' + payStatus;
            }
            if (paymentType !== '') {
                url += '&paymentType=' + encodeURIComponent(paymentType);
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
                    '<tr><td colspan="7" style="text-align:center;color:#ff4d4f;padding:40px;">加载失败，请稍后重试</td></tr>';
            });
        }

        /**
         * 渲染表格数据
         * @param {Array} list 费用列表
         */
        function renderTable(list) {
            var tbody = document.getElementById('tableBody');
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:40px;">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                var statusClass = item.payStatus === 1 ? 'status-paid' : 'status-unpaid';
                var statusText = item.payStatus === 1 ? '已缴' : '未缴';

                html += '<tr>';
                html += '<td>' + (item.id || '') + '</td>';
                html += '<td>' + (item.contractId || '') + '</td>';
                html += '<td>' + (item.paymentType || '') + '</td>';
                html += '<td>' + (item.amount ? '¥' + item.amount : '') + '</td>';
                html += '<td><span class="' + statusClass + '">' + statusText + '</span></td>';
                html += '<td>' + (item.payDate || '--') + '</td>';
                html += '<td class="action-btns">';
                if (item.payStatus === 0) {
                    html += '<button class="btn btn-success btn-small" onclick="markAsPaid(' + item.id + ')">标记已缴</button>';
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
         * 标记费用为已缴
         * @param {number} id 费用ID
         */
        function markAsPaid(id) {
            if (!confirm('确定要将该费用标记为已缴吗？')) {
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/payment/' + id + '/pay', {
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
         * 打开新增费用模态框
         */
        function openAddModal() {
            document.getElementById('addContractId').value = '';
            document.getElementById('addPaymentType').value = '租金';
            document.getElementById('addAmount').value = '';
            document.getElementById('addModal').classList.add('active');
        }

        /**
         * 关闭新增费用模态框
         */
        function closeAddModal() {
            document.getElementById('addModal').classList.remove('active');
        }

        /**
         * 提交新增费用
         */
        function submitAdd() {
            var contractId = document.getElementById('addContractId').value.trim();
            var paymentType = document.getElementById('addPaymentType').value;
            var amount = document.getElementById('addAmount').value.trim();

            if (!contractId) {
                alert('请输入合同编号');
                return;
            }
            if (!amount || parseFloat(amount) <= 0) {
                alert('请输入有效的金额');
                return;
            }

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/admin/payment', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    contractId: parseInt(contractId),
                    paymentType: paymentType,
                    amount: parseFloat(amount)
                })
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(result) {
                if (result.code === 200) {
                    closeAddModal();
                    loadData(currentPage);
                } else {
                    alert('添加失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('添加失败：', error);
                alert('添加失败，请稍后重试');
            });
        }
    </script>
</body>
</html>
