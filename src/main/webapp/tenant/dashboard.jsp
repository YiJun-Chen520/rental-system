<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>首页概览 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>首页概览</h2>
            <div class="stats-cards" id="statsCards">
                <div class="stat-card">
                    <div class="stat-label">生效合同数</div>
                    <div class="stat-value" id="activeContracts">--</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">待缴费用</div>
                    <div class="stat-value" id="pendingPayments">--</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">已缴总额</div>
                    <div class="stat-value" id="totalPaid">--</div>
                </div>
            </div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        // 页面加载时获取统计数据
        document.addEventListener('DOMContentLoaded', function() {
            loadStats();
        });

        function loadStats() {
            var token = localStorage.getItem('token');
            fetch('${pageContext.request.contextPath}/api/tenant/stats', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    var data = result.data;
                    document.getElementById('activeContracts').textContent = data.activeContracts || 0;
                    document.getElementById('pendingPayments').textContent = data.pendingPayments || 0;
                    document.getElementById('totalPaid').textContent = '¥' + (data.totalPaid || 0).toFixed(2);
                } else {
                    alert(result.message || '获取统计数据失败');
                }
            })
            .catch(function(error) {
                console.error('加载统计数据失败:', error);
            });
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
