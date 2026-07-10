<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人信息 - 房屋租赁系统</title>
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
            margin: 0;
        }

        .card {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            margin-bottom: 24px;
        }

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px 24px;
            border-bottom: 1px solid #f0f0f0;
        }

        .card-header h3 {
            margin: 0;
            font-size: 16px;
            color: #333;
        }

        .card-body {
            padding: 24px;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .info-item {
            display: flex;
            flex-direction: column;
        }

        .info-item .info-label {
            font-size: 13px;
            color: #999;
            margin-bottom: 6px;
        }

        .info-item .info-value {
            font-size: 15px;
            color: #333;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-size: 14px;
            color: #333;
            font-weight: 500;
        }

        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        .form-group input:focus {
            border-color: #1890ff;
        }

        .form-group input:disabled {
            background: #f5f5f5;
            color: #999;
            cursor: not-allowed;
        }

        .form-row {
            display: flex;
            gap: 20px;
        }

        .form-row .form-group {
            flex: 1;
        }

        .btn {
            padding: 8px 20px;
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

        .btn-sm {
            padding: 6px 16px;
            font-size: 13px;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 24px;
            padding-top: 16px;
            border-top: 1px solid #f0f0f0;
        }

        .password-section {
            margin-top: 8px;
        }

        .password-section .form-group {
            max-width: 400px;
        }

        .tip-text {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>

    <div class="main-content">
        <div class="page-header">
            <h1>个人信息</h1>
        </div>

        <!-- 基本信息卡片 -->
        <div class="card">
            <div class="card-header">
                <h3>基本信息</h3>
            </div>
            <div class="card-body">
                <div class="info-grid">
                    <div class="info-item">
                        <span class="info-label">用户名</span>
                        <div class="info-value" id="infoUsername">--</div>
                    </div>
                    <div class="info-item">
                        <span class="info-label">姓名</span>
                        <div class="info-value" id="infoName">--</div>
                    </div>
                    <div class="info-item">
                        <span class="info-label">手机号</span>
                        <div class="info-value" id="infoPhone">--</div>
                    </div>
                    <div class="info-item">
                        <span class="info-label">身份证号</span>
                        <div class="info-value" id="infoIdCard">--</div>
                    </div>
                    <div class="info-item">
                        <span class="info-label">银行账户</span>
                        <div class="info-value" id="infoBankAccount">--</div>
                    </div>
                    <div class="info-item">
                        <span class="info-label">注册时间</span>
                        <div class="info-value" id="infoCreateTime">--</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 编辑信息卡片 -->
        <div class="card">
            <div class="card-header">
                <h3>编辑信息</h3>
            </div>
            <div class="card-body">
                <form id="editForm" onsubmit="return false;">
                    <div class="form-group">
                        <label>用户名</label>
                        <input type="text" id="editUsername" disabled>
                        <div class="tip-text">用户名不可修改</div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>姓名</label>
                            <input type="text" id="editName" placeholder="请输入姓名">
                        </div>
                        <div class="form-group">
                            <label>手机号</label>
                            <input type="text" id="editPhone" placeholder="请输入手机号">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>身份证号</label>
                            <input type="text" id="editIdCard" placeholder="请输入身份证号">
                        </div>
                        <div class="form-group">
                            <label>银行账户</label>
                            <input type="text" id="editBankAccount" placeholder="请输入银行账户">
                        </div>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn btn-primary" onclick="saveProfile()">保存修改</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- 修改密码卡片 -->
        <div class="card">
            <div class="card-header">
                <h3>修改密码</h3>
            </div>
            <div class="card-body">
                <div class="password-section">
                    <div class="form-group">
                        <label>原密码</label>
                        <input type="password" id="oldPassword" placeholder="请输入原密码">
                    </div>
                    <div class="form-group">
                        <label>新密码</label>
                        <input type="password" id="newPassword" placeholder="请输入新密码（至少6位）">
                    </div>
                    <div class="form-group">
                        <label>确认新密码</label>
                        <input type="password" id="confirmPassword" placeholder="请再次输入新密码">
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn btn-primary" onclick="changePassword()">修改密码</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        /**
         * 页面初始化，加载个人信息
         */
        document.addEventListener('DOMContentLoaded', function() {
            loadProfile();
        });

        /**
         * 加载个人信息
         */
        function loadProfile() {
            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/auth?action=info', {
                method: 'GET',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    var data = result.data;

                    // 显示信息
                    document.getElementById('infoUsername').textContent = data.username || '--';
                    document.getElementById('infoName').textContent = data.name || '--';
                    document.getElementById('infoPhone').textContent = data.phone || '--';
                    document.getElementById('infoIdCard').textContent = data.idCard || '--';
                    document.getElementById('infoBankAccount').textContent = data.bankAccount || '--';
                    document.getElementById('infoCreateTime').textContent = data.createTime || '--';

                    // 填充编辑表单
                    document.getElementById('editUsername').value = data.username || '';
                    document.getElementById('editName').value = data.name || '';
                    document.getElementById('editPhone').value = data.phone || '';
                    document.getElementById('editIdCard').value = data.idCard || '';
                    document.getElementById('editBankAccount').value = data.bankAccount || '';
                } else {
                    alert('获取个人信息失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('加载个人信息失败：', error);
            });
        }

        /**
         * 保存个人信息
         */
        function saveProfile() {
            var name = document.getElementById('editName').value.trim();
            var phone = document.getElementById('editPhone').value.trim();
            var idCard = document.getElementById('editIdCard').value.trim();
            var bankAccount = document.getElementById('editBankAccount').value.trim();

            // 简单验证
            if (!name) { alert('请输入姓名'); return; }
            if (phone && !/^1\d{10}$/.test(phone)) { alert('请输入正确的手机号'); return; }
            if (idCard && !/^\d{17}[\dXx]$/.test(idCard)) { alert('请输入正确的身份证号'); return; }

            var data = {
                name: name,
                phone: phone,
                idCard: idCard,
                bankAccount: bankAccount
            };

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/auth?action=update', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('个人信息更新成功');
                    loadProfile();
                    // 更新localStorage中的用户信息
                    var user = JSON.parse(localStorage.getItem('user') || '{}');
                    user.name = name;
                    localStorage.setItem('user', JSON.stringify(user));
                } else {
                    alert('更新失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('更新个人信息失败：', error);
                alert('操作失败，请稍后重试');
            });
        }

        /**
         * 修改密码
         */
        function changePassword() {
            var oldPassword = document.getElementById('oldPassword').value;
            var newPassword = document.getElementById('newPassword').value;
            var confirmPassword = document.getElementById('confirmPassword').value;

            if (!oldPassword) { alert('请输入原密码'); return; }
            if (!newPassword) { alert('请输入新密码'); return; }
            if (newPassword.length < 6) { alert('新密码长度不能少于6位'); return; }
            if (newPassword !== confirmPassword) { alert('两次输入的新密码不一致'); return; }

            var data = {
                oldPassword: oldPassword,
                newPassword: newPassword
            };

            var token = localStorage.getItem('token');
            fetch(getContextPath() + '/api/owner/auth?action=changePassword', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('密码修改成功，请重新登录');
                    // 清除登录状态
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    window.location.href = getContextPath() + '/login.jsp';
                } else {
                    alert('修改失败：' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('修改密码失败：', error);
                alert('操作失败，请稍后重试');
            });
        }
    </script>
</body>
</html>
