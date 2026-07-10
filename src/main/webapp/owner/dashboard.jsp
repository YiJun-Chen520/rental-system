<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>房东概览 - 房屋租赁系统</title>
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

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 24px;
        }

        .stat-card {
            background: #fff;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        }

        .stat-card .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 16px;
        }

        .stat-card .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #333;
            margin-bottom: 4px;
        }

        .stat-card .stat-label {
            font-size: 14px;
            color: #999;
        }

        .stat-card:nth-child(1) .stat-icon { background: #e6f7ff; color: #1890ff; }
        .stat-card:nth-child(2) .stat-icon { background: #f6ffed; color: #52c41a; }
        .stat-card:nth-child(3) .stat-icon { background: #fff7e6; color: #fa8c16; }
        .stat-card:nth-child(4) .stat-icon { background: #f9f0ff; color: #722ed1; }
        .stat-card:nth-child(5) .stat-icon { background: #fff1f0; color: #f5222d; }

        .welcome-section {
            background: linear-gradient(135deg, #1890ff, #722ed1);
            border-radius: 8px;
            padding: 32px;
            color: #fff;
            margin-bottom: 24px;
        }

        .welcome-section h2 {
            margin: 0 0 8px 0;
            font-size: 20px;
        }

        .welcome-section p {
            margin: 0;
            opacity: 0.85;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="welcome-section">
            <h2 id="welcomeText">欢迎回来！</h2>
            <p>这是您的房东控制台，可以在这里管理房源、合同和费用信息。</p>
        </div>

        <div class="page-header">
            <h1>数据概览</h1>
            <p>查看您的房源和业务概况</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">&#127968;</div>
                <div class="stat-value" id="totalHouses">--</div>
                <div class="stat-label">我的房源数</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#9989;</div>
                <div class="stat-value" id="freeHouses">--</div>
                <div class="stat-label">空闲房源</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128100;</div>
                <div class="stat-value" id="rentedHouses">--</div>
                <div class="stat-label">已租房源</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128196;</div>
                <div class="stat-value" id="activeContracts">--</div>
                <div class="stat-label">生效合同</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#128176;</div>
                <div class="stat-value" id="pendingPayments">--</div>
                <div class="stat-label">待缴费用</div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        /**
         * 页面初始化，加载统计数据
         */
        document.addEventListener('DOMContentLoaded', function() {
            // 显示欢迎信息
            var user = JSON.parse(localStorage.getItem('user') || '{}');
            if (user.name) {
                document.getElementById('welcomeText').textContent = '欢迎回来，' + user.name + '！';
            }

            loadStats();
        });

        /**
         * 加载统计数据
         */
        function loadStats() {
            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/stats', {
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
                    var data = result.data;
                    document.getElementById('totalHouses').textContent = data.totalHouses || 0;
                    document.getElementById('freeHouses').textContent = data.freeHouses || 0;
                    document.getElementById('rentedHouses').textContent = data.rentedHouses || 0;
                    document.getElementById('activeContracts').textContent = data.activeContracts || 0;
                    document.getElementById('pendingPayments').textContent = data.pendingPayments || 0;
                } else {
                    alert('获取统计数据失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载统计数据失败：', error);
            });
        }
    </script>
</body>
</html>
