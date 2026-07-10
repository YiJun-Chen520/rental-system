<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>房源管理 - 房屋租赁系统</title>
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
            flex-wrap: wrap;
        }

        .search-bar input,
        .search-bar select {
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
        }

        .search-bar input:focus,
        .search-bar select:focus {
            border-color: #1890ff;
        }

        .search-bar input[type="text"] {
            width: 240px;
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

        .btn-danger {
            background: #ff4d4f;
            color: #fff;
        }

        .btn-danger:hover {
            background: #ff7875;
        }

        .btn-sm {
            padding: 4px 8px;
            font-size: 12px;
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

        .status-free {
            background: #f6ffed;
            color: #52c41a;
            border: 1px solid #b7eb8f;
        }

        .status-rented {
            background: #e6f7ff;
            color: #1890ff;
            border: 1px solid #91d5ff;
        }

        .status-offline {
            background: #f5f5f5;
            color: #999;
            border: 1px solid #d9d9d9;
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
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
            box-sizing: border-box;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            border-color: #1890ff;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 80px;
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
            <h1>房源管理</h1>
            <button class="btn btn-primary" onclick="openAddModal()">+ 添加房源</button>
        </div>

        <div class="search-bar">
            <input type="text" id="keyword" placeholder="搜索地址/户型" onkeyup="if(event.key==='Enter')loadHouses()">
            <select id="statusFilter" onchange="loadHouses()">
                <option value="">全部状态</option>
                <option value="free">空闲</option>
                <option value="rented">已租</option>
                <option value="offline">已下架</option>
            </select>
            <button class="btn btn-primary" onclick="loadHouses()">搜索</button>
        </div>

        <div class="data-table-wrapper">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>编号</th>
                        <th>地址</th>
                        <th>户型</th>
                        <th>面积(㎡)</th>
                        <th>月租金(元)</th>
                        <th>状态</th>
                        <th>发布时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="houseTableBody">
                    <tr><td colspan="8" class="empty-tip">加载中...</td></tr>
                </tbody>
            </table>
            <div class="pagination" id="pagination"></div>
        </div>
    </div>

    <!-- 添加/编辑房源模态框 -->
    <div class="modal-overlay" id="houseModal">
        <div class="modal">
            <div class="modal-header">
                <h3 id="modalTitle">添加房源</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="houseId">
                <div class="form-group">
                    <label>地址 <span class="required">*</span></label>
                    <input type="text" id="address" placeholder="请输入详细地址">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>户型 <span class="required">*</span></label>
                        <input type="text" id="houseType" placeholder="如：2室1厅1卫">
                    </div>
                    <div class="form-group">
                        <label>面积(㎡) <span class="required">*</span></label>
                        <input type="number" id="area" placeholder="请输入面积" step="0.01">
                    </div>
                </div>
                <div class="form-group">
                    <label>月租金(元) <span class="required">*</span></label>
                    <input type="number" id="rentPrice" placeholder="请输入月租金" step="0.01">
                </div>
                <div class="form-group">
                    <label>配套设施</label>
                    <input type="text" id="facilities" placeholder="如：空调、冰箱、洗衣机、WiFi">
                </div>
                <div class="form-group">
                    <label>描述</label>
                    <textarea id="description" placeholder="请输入房源描述信息"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" onclick="closeModal()">取消</button>
                <button class="btn btn-primary" onclick="saveHouse()">保存</button>
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
            loadHouses();
        });

        /**
         * 加载房源列表
         */
        function loadHouses() {
            var keyword = document.getElementById('keyword').value;
            var status = document.getElementById('statusFilter').value;
            var token = localStorage.getItem('token');

            var url = getContextPath() + '/api/owner/house?page=' + currentPage + '&pageSize=' + pageSize;
            if (status) url += '&status=' + status;
            if (keyword) url += '&keyword=' + encodeURIComponent(keyword);

            fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    renderTable(result.data.list || []);
                    renderPagination(result.data.total || 0);
                } else {
                    alert('获取房源列表失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载房源列表失败：', error);
            });
        }

        /**
         * 渲染房源表格
         */
        function renderTable(list) {
            var tbody = document.getElementById('houseTableBody');
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="empty-tip">暂无数据</td></tr>';
                return;
            }

            var html = '';
            list.forEach(function(item) {
                var statusClass = '';
                var statusText = '';
                if (item.status === 'free') { statusClass = 'status-free'; statusText = '空闲'; }
                else if (item.status === 'rented') { statusClass = 'status-rented'; statusText = '已租'; }
                else { statusClass = 'status-offline'; statusText = '已下架'; }

                html += '<tr>';
                html += '<td>' + item.id + '</td>';
                html += '<td>' + escapeHtml(item.address) + '</td>';
                html += '<td>' + escapeHtml(item.houseType) + '</td>';
                html += '<td>' + item.area + '</td>';
                html += '<td>' + item.rentPrice + '</td>';
                html += '<td><span class="status-tag ' + statusClass + '">' + statusText + '</span></td>';
                html += '<td>' + (item.createTime || '--') + '</td>';
                html += '<td class="action-btns">';
                html += '<button class="btn-link" onclick="openEditModal(' + item.id + ')">编辑</button>';
                if (item.status !== 'offline') {
                    html += '<button class="btn-link danger" onclick="deleteHouse(' + item.id + ', \'' + item.status + '\')">' + (item.status === 'rented' ? '下架' : '删除') + '</button>';
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
            loadHouses();
        }

        /**
         * 打开添加模态框
         */
        function openAddModal() {
            document.getElementById('modalTitle').textContent = '添加房源';
            document.getElementById('houseId').value = '';
            document.getElementById('address').value = '';
            document.getElementById('houseType').value = '';
            document.getElementById('area').value = '';
            document.getElementById('rentPrice').value = '';
            document.getElementById('facilities').value = '';
            document.getElementById('description').value = '';
            document.getElementById('houseModal').classList.add('active');
        }

        /**
         * 打开编辑模态框
         */
        function openEditModal(id) {
            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/house?id=' + id, {
                method: 'GET',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    var item = result.data;
                    document.getElementById('modalTitle').textContent = '编辑房源';
                    document.getElementById('houseId').value = item.id;
                    document.getElementById('address').value = item.address || '';
                    document.getElementById('houseType').value = item.houseType || '';
                    document.getElementById('area').value = item.area || '';
                    document.getElementById('rentPrice').value = item.rentPrice || '';
                    document.getElementById('facilities').value = item.facilities || '';
                    document.getElementById('description').value = item.description || '';
                    document.getElementById('houseModal').classList.add('active');
                } else {
                    alert('获取房源信息失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('获取房源信息失败：', error);
            });
        }

        /**
         * 关闭模态框
         */
        function closeModal() {
            document.getElementById('houseModal').classList.remove('active');
        }

        /**
         * 保存房源（新增/编辑）
         */
        function saveHouse() {
            var id = document.getElementById('houseId').value;
            var address = document.getElementById('address').value.trim();
            var houseType = document.getElementById('houseType').value.trim();
            var area = document.getElementById('area').value;
            var rentPrice = document.getElementById('rentPrice').value;
            var facilities = document.getElementById('facilities').value.trim();
            var description = document.getElementById('description').value.trim();

            // 表单验证
            if (!address) { alert('请输入地址'); return; }
            if (!houseType) { alert('请输入户型'); return; }
            if (!area || area <= 0) { alert('请输入有效面积'); return; }
            if (!rentPrice || rentPrice <= 0) { alert('请输入有效月租金'); return; }

            var data = {
                address: address,
                houseType: houseType,
                area: parseFloat(area),
                rentPrice: parseFloat(rentPrice),
                facilities: facilities,
                description: description
            };
            if (id) data.id = parseInt(id);

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/house', {
                method: id ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert(id ? '房源更新成功' : '房源添加成功');
                    closeModal();
                    loadHouses();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('保存房源失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 删除/下架房源
         */
        function deleteHouse(id, status) {
            var msg = status === 'rented' ? '确定要下架该房源吗？' : '确定要删除该房源吗？此操作不可恢复。';
            if (!confirm(msg)) return;

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/house?id=' + id, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert(status === 'rented' ? '房源已下架' : '房源已删除');
                    loadHouses();
                } else {
                    alert('操作失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('删除房源失败：', error);
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
