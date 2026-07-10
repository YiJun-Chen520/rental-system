<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>费用管理 - 房屋租赁系统</title>
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
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .page-header h1 {
            font-size: 24px;
            color: #333;
            margin: 0;
        }

        .search-bar {
            background: #fff;
            padding: 16px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            margin-bottom: 16px;
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .search-bar select {
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
        }

        .search-bar select:focus {
            border-color: #1890ff;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.2s;
        }

        .btn-primary {
            background: #1890ff;
            color: #fff;
        }

        .btn-primary:hover {
            background: #40a9ff;
        }

        .btn-link {
            background: none;
            border: none;
            color: #1890ff;
            cursor: pointer;
            padding: 4px 8px;
            font-size: 13px;
        }

        .btn-link:hover {
            color: #40a9ff;
        }

        .data-table-wrapper {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            overflow: hidden;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .data-table th {
            background: #fafafa;
            padding: 12px 16px;
            text-align: left;
            font-weight: 600;
            color: #333;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
            white-space: nowrap;
        }

        .data-table td {
            padding: 12px 16px;
            border-bottom: 1px solid #f0f0f0;
            color: #666;
            font-size: 14px;
        }

        .data-table tr:hover {
            background: #f5f5f5;
        }

        .status-tag {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
        }

        .status-unpaid {
            background: #fff1f0;
            color: #ff4d4f;
            border: 1px solid #ffa39e;
        }

        .status-paid {
            background: #f6ffed;
            color: #52c41a;
            border: 1px solid #b7eb8f;
        }

        .status-overdue {
            background: #fff7e6;
            color: #fa8c16;
            border: 1px solid #ffd591;
        }

        .pagination {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            padding: 16px;
            gap: 8px;
        }

        .pagination button {
            padding: 6px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            background: #fff;
            cursor: pointer;
            font-size: 14px;
            color: #333;
        }

        .pagination button:hover:not(:disabled) {
            border-color: #1890ff;
            color: #1890ff;
        }

        .pagination button:disabled {
            cursor: not-allowed;
            color: #d9d9d9;
        }

        .pagination button.active {
            background: #1890ff;
            color: #fff;
            border-color: #1890ff;
        }

        .pagination .page-info {
            font-size: 14px;
            color: #666;
            margin: 0 8px;
        }

        /* 模态框样式 */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
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
            width: 480px;
            max-width: 90vw;
            max-height: 85vh;
            overflow-y: auto;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px 24px;
            border-bottom: 1px solid #f0f0f0;
        }

        .modal-header h3 {
            margin: 0;
            font-size: 18px;
            color: #333;
        }

        .modal-close {
            background: none;
            border: none;
            font-size: 20px;
            cursor: pointer;
            color: #999;
            padding: 0 4px;
        }

        .modal-close:hover {
            color: #333;
        }

        .modal-body {
            padding: 24px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            color: #333;
            font-weight: 500;
        }

        .form-group label .required {
            color: #ff4d4f;
            margin-left: 2px;
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
        }

        .modal-footer {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            padding: 16px 24px;
            border-top: 1px solid #f0f0f0;
        }

        .empty-tip {
            text-align: center;
            padding: 48px;
            color: #999;
            font-size: 14px;
        }

        .payment-type-tag {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
            background: #e6f7ff;
            color: #1890ff;
            border: 1px solid #91d5ff;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>费用管理</h1>
            <button class="btn btn-primary" onclick="openAddModal()">+ 添加费用</button>
        </div>

        <div class="search-bar">
            <select id="payStatusFilter" onchange="loadPayments()">
                <option value="">全部状态</option>
                <option value="unpaid">未缴纳</option>
                <option value="paid">已缴纳</option>
                <option value="overdue">已逾期</option>
            </select>
        </div>

        <div class="data-table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>合同编号</th>
                        <th>费用类型</th>
                        <th>金额(元)</th>
                        <th>缴费状态</th>
                        <th>缴费日期</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="paymentTableBody">
                    <tr><td colspan="7" class="empty-tip">加载中...</td></tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </div>
    </div>

    <!-- 添加费用模态框 -->
    <div class="modal-overlay" id="paymentModal">
        <div class="modal">
            <div class="modal-header">
                <h3>添加费用</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>合同编号 <span class="required">*</span></label>
                    <input type="number" id="contractId" placeholder="请输入合同编号">
                </div>
                <div class="form-group">
                    <label>费用类型 <span class="required">*</span></label>
                    <select id="paymentType">
                        <option value="">请选择费用类型</option>
                        <option value="rent">租金</option>
                        <option value="deposit">押金</option>
                        <option value="utility">水电费</option>
                        <option value="property">物业费</option>
                        <option value="other">其他</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>金额(元) <span class="required">*</span></label>
                    <input type="number" id="amount" placeholder="请输入金额" step="0.01">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" onclick="closeModal()">取消</button>
                <button class="btn btn-primary" onclick="savePayment()">添加</button>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        var currentPage = 1;
        var pageSize = 10;

        /**
         * 费用类型映射
         */
        var paymentTypeMap = {
            'rent': '租金',
            'deposit': '押金',
            'utility': '水电费',
            'property': '物业费',
            'other': '其他'
        };

        /**
         * 页面初始化
         */
        document.addEventListener('DOMContentLoaded', function() {
            loadPayments();
        });

        /**
         * 加载费用列表
         */
        function loadPayments() {
            var payStatus = document.getElementById('payStatusFilter').value;
            var token = localStorage.getItem('token');

            var url = getContextPath() + '/api/owner/payment?page=' + currentPage + '&pageSize=' + pageSize;
            if (payStatus) url += '&payStatus=' + payStatus;

            fetch(url, {
                method: 'GET',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    renderTable(result.data.list || []);
                    renderPagination(result.data.total || 0);
                } else {
                    alert('获取费用列表失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载费用列表失败：', error);
            });
        }

        /**
         * 渲染费用表格
         */
        function renderTable(list) {
            var tbody = document.getElementById('paymentTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="empty-tip">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                var statusClass = '';
                var statusText = '';
                if (item.payStatus === 'paid') { statusClass = 'status-paid'; statusText = '已缴纳'; }
                else if (item.payStatus === 'overdue') { statusClass = 'status-overdue'; statusText = '已逾期'; }
                else { statusClass = 'status-unpaid'; statusText = '未缴纳'; }

                var typeText = paymentTypeMap[item.paymentType] || item.paymentType;

                html += '<tr>';
                html += '<td>' + item.id + '</td>';
                html += '<td>' + item.contractId + '</td>';
                html += '<td><span class="payment-type-tag">' + escapeHtml(typeText) + '</span></td>';
                html += '<td>' + item.amount + '</td>';
                html += '<td><span class="status-tag ' + statusClass + '">' + statusText + '</span></td>';
                html += '<td>' + (item.payDate || '--') + '</td>';
                html += '<td>';
                if (item.payStatus === 'unpaid' || item.payStatus === 'overdue') {
                    html += '<button class="btn-link" onclick="markAsPaid(' + item.id + ')">标记已缴</button>';
                }
                html += '</td>';
                html += '</tr>';
            });
            tbody.innerHTML = html;
        }

        /**
         * 渲染分页
         */
        function renderPagination(total) {
            var container = document.getElementById('pagination');
            var totalPages = Math.ceil(total / pageSize);
            if (totalPages <= 1) {
                container.innerHTML = '<span class="page-info">共 ' + total + ' 条记录</span>';
                return;
            }

            var html = '<button onclick="goPage(' + (currentPage - 1) + ')" ' + (currentPage <= 1 ? 'disabled' : '') + '>上一页</button>';
            for (var i = 1; i <= totalPages; i++) {
                html += '<button class="' + (i === currentPage ? 'active' : '') + '" onclick="goPage(' + i + ')">' + i + '</button>';
            }
            html += '<button onclick="goPage(' + (currentPage + 1) + ')" ' + (currentPage >= totalPages ? 'disabled' : '') + '>下一页</button>';
            html += '<span class="page-info">共 ' + total + ' 条记录</span>';
            container.innerHTML = html;
        }

        /**
         * 翻页
         */
        function goPage(page) {
            currentPage = page;
            loadPayments();
        }

        /**
         * 打开添加费用模态框
         */
        function openAddModal() {
            document.getElementById('contractId').value = '';
            document.getElementById('paymentType').value = '';
            document.getElementById('amount').value = '';
            document.getElementById('paymentModal').classList.add('active');
        }

        /**
         * 关闭模态框
         */
        function closeModal() {
            document.getElementById('paymentModal').classList.remove('active');
        }

        /**
         * 保存费用
         */
        function savePayment() {
            var contractId = document.getElementById('contractId').value;
            var paymentType = document.getElementById('paymentType').value;
            var amount = document.getElementById('amount').value;

            if (!contractId) { alert('请输入合同编号'); return; }
            if (!paymentType) { alert('请选择费用类型'); return; }
            if (!amount || amount <= 0) { alert('请输入有效金额'); return; }

            var data = {
                contractId: parseInt(contractId),
                paymentType: paymentType,
                amount: parseFloat(amount)
            };

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('费用添加成功');
                    closeModal();
                    loadPayments();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('添加费用失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 标记费用为已缴
         */
        function markAsPaid(id) {
            if (!confirm('确定要将该费用标记为已缴纳吗？')) return;

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/payment?action=pay&id=' + id, {
                method: 'PUT',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('费用已标记为已缴纳');
                    loadPayments();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('标记缴费失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * HTML转义
         */
        function escapeHtml(text) {
            if (!text) return '';
            var div = document.createElement('div');
            div.appendChild(document.createTextNode(text));
            return div.innerHTML;
        }
    </script>
</body>
</html>
