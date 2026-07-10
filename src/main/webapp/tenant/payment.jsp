<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的费用 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>我的费用</h2>
            <div class="filter-bar">
                <select id="payStatusFilter" onchange="filterByPayStatus()">
                    <option value="">全部状态</option>
                    <option value="未缴">未缴</option>
                    <option value="已缴">已缴</option>
                </select>
            </div>
            <table class="data-table">
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
                <tbody id="paymentTableBody">
                    <tr>
                        <td colspan="7">加载中...</td>
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
            loadPayments();
        });

        function loadPayments() {
            var token = localStorage.getItem('token');
            var payStatus = document.getElementById('payStatusFilter').value;

            var url = '/api/tenant/payment?page=' + currentPage + '&pageSize=' + pageSize;
            if (payStatus) url += '&payStatus=' + encodeURIComponent(payStatus);

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
                    alert(result.message || '获取费用列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载费用列表失败:', error);
            });
        }

        function renderTable(payments) {
            var tbody = document.getElementById('paymentTableBody');
            if (payments.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7">暂无数据</td></tr>';
                return;
            }
            var html = '';
            for (var i = 0; i < payments.length; i++) {
                var p = payments[i];
                html += '<tr>';
                html += '<td>' + p.id + '</td>';
                html += '<td>' + (p.contractId || '') + '</td>';
                html += '<td>' + (p.paymentType || '') + '</td>';
                html += '<td>¥' + (p.amount || 0) + '</td>';
                html += '<td><span class="status-tag status-' + (p.payStatus === '已缴' ? 'paid' : 'unpaid') + '">' + (p.payStatus || '') + '</span></td>';
                html += '<td>' + (p.payDate || '未缴费') + '</td>';
                html += '<td>';
                if (p.payStatus === '未缴') {
                    html += '<button class="btn-pay" onclick="confirmPay(' + p.id + ', ' + p.amount + ')">缴费</button>';
                } else {
                    html += '-';
                }
                html += '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        }

        function confirmPay(id, amount) {
            if (confirm('确认支付 ¥' + amount + ' 元？')) {
                payPayment(id);
            }
        }

        function payPayment(id) {
            var token = localStorage.getItem('token');
            fetch('/api/tenant/payment?action=pay&id=' + id, {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('支付成功！');
                    loadPayments();
                } else {
                    alert(result.message || '支付失败');
                }
            })
            .catch(function(error) {
                console.error('支付失败:', error);
                alert('支付失败，请稍后重试');
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
            loadPayments();
        }

        function filterByPayStatus() {
            currentPage = 1;
            loadPayments();
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
