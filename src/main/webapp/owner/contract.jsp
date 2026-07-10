<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>合同管理 - 房屋租赁系统</title>
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

        .btn-link.danger {
            color: #ff4d4f;
        }

        .btn-link.danger:hover {
            color: #ff7875;
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

        .status-active {
            background: #f6ffed;
            color: #52c41a;
            border: 1px solid #b7eb8f;
        }

        .status-expired {
            background: #f5f5f5;
            color: #999;
            border: 1px solid #d9d9d9;
        }

        .status-terminated {
            background: #fff1f0;
            color: #ff4d4f;
            border: 1px solid #ffa39e;
        }

        .status-pending {
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
            width: 560px;
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

        .form-row {
            display: flex;
            gap: 16px;
        }

        .form-row .form-group {
            flex: 1;
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

        .action-btns {
            white-space: nowrap;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>合同管理</h1>
            <button class="btn btn-primary" onclick="openAddModal()">+ 创建合同</button>
        </div>

        <div class="search-bar">
            <select id="statusFilter" onchange="loadContracts()">
                <option value="">全部状态</option>
                <option value="pending">待生效</option>
                <option value="active">生效中</option>
                <option value="expired">已到期</option>
                <option value="terminated">已终止</option>
            </select>
        </div>

        <div class="data-table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>房源地址</th>
                        <th>租户</th>
                        <th>签订日期</th>
                        <th>起始日期</th>
                        <th>终止日期</th>
                        <th>月租金(元)</th>
                        <th>押金(元)</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="contractTableBody">
                    <tr><td colspan="10" class="empty-tip">加载中...</td></tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </div>
    </div>

    <!-- 创建合同模态框 -->
    <div class="modal-overlay" id="contractModal">
        <div class="modal">
            <div class="modal-header">
                <h3>创建合同</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-row">
                    <div class="form-group">
                        <label>房源 <span class="required">*</span></label>
                        <select id="houseId">
                            <option value="">请选择房源</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>租户ID <span class="required">*</span></label>
                        <input type="number" id="tenantId" placeholder="请输入租户ID">
                    </div>
                </div>
                <div class="form-group">
                    <label>签订日期 <span class="required">*</span></label>
                    <input type="date" id="signDate">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>起始日期 <span class="required">*</span></label>
                        <input type="date" id="startDate">
                    </div>
                    <div class="form-group">
                        <label>终止日期 <span class="required">*</span></label>
                        <input type="date" id="endDate">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>月租金(元) <span class="required">*</span></label>
                        <input type="number" id="monthlyRent" placeholder="请输入月租金" step="0.01">
                    </div>
                    <div class="form-group">
                        <label>押金(元) <span class="required">*</span></label>
                        <input type="number" id="deposit" placeholder="请输入押金" step="0.01">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" onclick="closeModal()">取消</button>
                <button class="btn btn-primary" onclick="saveContract()">创建</button>
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
            loadContracts();
        });

        /**
         * 加载合同列表
         */
        function loadContracts() {
            var status = document.getElementById('statusFilter').value;
            var token = localStorage.getItem('token');

            var url = '/api/owner/contract?page=' + currentPage + '&pageSize=' + pageSize;
            if (status) url += '&status=' + status;

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
                    alert('获取合同列表失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载合同列表失败：', error);
            });
        }

        /**
         * 渲染合同表格
         */
        function renderTable(list) {
            var tbody = document.getElementById('contractTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="10" class="empty-tip">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                var statusClass = '';
                var statusText = '';
                if (item.status === 'active') { statusClass = 'status-active'; statusText = '生效中'; }
                else if (item.status === 'expired') { statusClass = 'status-expired'; statusText = '已到期'; }
                else if (item.status === 'terminated') { statusClass = 'status-terminated'; statusText = '已终止'; }
                else { statusClass = 'status-pending'; statusText = '待生效'; }

                html += '<tr>';
                html += '<td>' + item.id + '</td>';
                html += '<td>' + escapeHtml(item.houseAddress || '--') + '</td>';
                html += '<td>' + escapeHtml(item.tenantName || '--') + '</td>';
                html += '<td>' + (item.signDate || '--') + '</td>';
                html += '<td>' + (item.startDate || '--') + '</td>';
                html += '<td>' + (item.endDate || '--') + '</td>';
                html += '<td>' + item.monthlyRent + '</td>';
                html += '<td>' + item.deposit + '</td>';
                html += '<td><span class="status-tag ' + statusClass + '">' + statusText + '</span></td>';
                html += '<td class="action-btns">';
                if (item.status === 'active') {
                    html += '<button class="btn-link danger" onclick="terminateContract(' + item.id + ')">终止</button>';
                }
                if (item.status === 'active' || item.status === 'pending') {
                    html += '<button class="btn-link" onclick="expireContract(' + item.id + ')">到期</button>';
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
            loadContracts();
        }

        /**
         * 打开创建合同模态框
         */
        function openAddModal() {
            document.getElementById('tenantId').value = '';
            document.getElementById('signDate').value = '';
            document.getElementById('startDate').value = '';
            document.getElementById('endDate').value = '';
            document.getElementById('monthlyRent').value = '';
            document.getElementById('deposit').value = '';

            // 加载空闲房源列表
            loadFreeHouses();
            document.getElementById('contractModal').classList.add('active');
        }

        /**
         * 加载空闲房源下拉列表
         */
        function loadFreeHouses() {
            var token = localStorage.getItem('token');
            fetch('/api/owner/house?page=1&pageSize=100&status=free', {
                method: 'GET',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var select = document.getElementById('houseId');
                select.innerHTML = '<option value="">请选择房源</option>';
                if (result.code === 200 && result.data && result.data.list) {
                    result.data.list.forEach(function(house) {
                        select.innerHTML += '<option value="' + house.id + '" data-rent="' + house.rentPrice + '">' + escapeHtml(house.address) + '</option>';
                    });
                }
            })
            .catch(function(error) {
                console.error('加载房源列表失败：', error);
            });
        }

        /**
         * 关闭模态框
         */
        function closeModal() {
            document.getElementById('contractModal').classList.remove('active');
        }

        /**
         * 保存合同
         */
        function saveContract() {
            var houseId = document.getElementById('houseId').value;
            var tenantId = document.getElementById('tenantId').value;
            var signDate = document.getElementById('signDate').value;
            var startDate = document.getElementById('startDate').value;
            var endDate = document.getElementById('endDate').value;
            var monthlyRent = document.getElementById('monthlyRent').value;
            var deposit = document.getElementById('deposit').value;

            if (!houseId) { alert('请选择房源'); return; }
            if (!tenantId) { alert('请输入租户ID'); return; }
            if (!signDate) { alert('请选择签订日期'); return; }
            if (!startDate) { alert('请选择起始日期'); return; }
            if (!endDate) { alert('请选择终止日期'); return; }
            if (!monthlyRent || monthlyRent <= 0) { alert('请输入有效月租金'); return; }
            if (!deposit || deposit < 0) { alert('请输入有效押金'); return; }

            var data = {
                houseId: parseInt(houseId),
                tenantId: parseInt(tenantId),
                signDate: signDate,
                startDate: startDate,
                endDate: endDate,
                monthlyRent: parseFloat(monthlyRent),
                deposit: parseFloat(deposit)
            };

            var token = localStorage.getItem('token');
            fetch('/api/owner/contract', {
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
                    alert('合同创建成功');
                    closeModal();
                    loadContracts();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('创建合同失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 终止合同
         */
        function terminateContract(id) {
            if (!confirm('确定要终止该合同吗？终止后不可恢复。')) return;

            var token = localStorage.getItem('token');
            fetch('/api/owner/contract?action=terminate&id=' + id, {
                method: 'PUT',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('合同已终止');
                    loadContracts();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('终止合同失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 标记合同到期
         */
        function expireContract(id) {
            if (!confirm('确定要将该合同标记为到期吗？')) return;

            var token = localStorage.getItem('token');
            fetch('/api/owner/contract?action=expire&id=' + id, {
                method: 'PUT',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('合同已标记为到期');
                    loadContracts();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('到期操作失败：', error);
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
