<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 房屋租赁管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        /* 登录页面样式 */
        body {
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        .login-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            width: 100%;
            max-width: 420px;
            margin: 20px;
        }

        .login-card h1 {
            text-align: center;
            color: #333;
            font-size: 24px;
            margin: 0 0 30px 0;
        }

        /* 角色选择标签 */
        .role-tabs {
            display: flex;
            border-bottom: 2px solid #e9ecef;
            margin-bottom: 25px;
        }

        .role-tab {
            flex: 1;
            text-align: center;
            padding: 12px 0;
            cursor: pointer;
            color: #666;
            font-size: 14px;
            border-bottom: 2px solid transparent;
            margin-bottom: -2px;
            transition: all 0.3s ease;
        }

        .role-tab:hover {
            color: #667eea;
        }

        .role-tab.active {
            color: #667eea;
            border-bottom-color: #667eea;
            font-weight: 500;
        }

        /* 表单样式 */
        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-size: 14px;
        }

        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s ease;
            box-sizing: border-box;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        /* 登录按钮 */
        .login-btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .login-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }

        .login-btn:active {
            transform: translateY(0);
        }

        /* 注册链接 */
        .register-link {
            text-align: center;
            margin-top: 20px;
            font-size: 14px;
            color: #666;
        }

        .register-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        /* 隐藏元素 */
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <div class="login-card">
        <h1>房屋租赁管理系统</h1>

        <!-- 角色选择标签 -->
        <div class="role-tabs">
            <div class="role-tab active" data-role="owner" onclick="switchRole('owner')">房东登录</div>
            <div class="role-tab" data-role="tenant" onclick="switchRole('tenant')">租户登录</div>
            <div class="role-tab" data-role="admin" onclick="switchRole('admin')">管理员登录</div>
        </div>

        <!-- 登录表单 -->
        <form id="loginForm" onsubmit="handleLogin(event)">
            <div class="form-group">
                <label id="accountLabel" for="account">手机号</label>
                <input type="text" id="account" name="account" placeholder="请输入手机号" required>
            </div>

            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password" placeholder="请输入密码" required>
            </div>

            <button type="submit" class="login-btn">登 录</button>
        </form>

        <!-- 注册链接（仅房东和租户显示） -->
        <div class="register-link" id="registerLink">
            还没有账号？<a href="${pageContext.request.contextPath}/register.jsp">立即注册</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        // 当前选中的角色
        let currentRole = 'owner';

        /**
         * 切换角色
         * @param {string} role - 角色类型: owner/tenant/admin
         */
        function switchRole(role) {
            currentRole = role;

            // 更新标签样式
            document.querySelectorAll('.role-tab').forEach(tab => {
                tab.classList.remove('active');
                if (tab.dataset.role === role) {
                    tab.classList.add('active');
                }
            });

            // 更新输入框标签
            const accountLabel = document.getElementById('accountLabel');
            const accountInput = document.getElementById('account');

            if (role === 'admin') {
                accountLabel.textContent = '用户名';
                accountInput.placeholder = '请输入用户名';
            } else {
                accountLabel.textContent = '手机号';
                accountInput.placeholder = '请输入手机号';
            }

            // 显示/隐藏注册链接
            const registerLink = document.getElementById('registerLink');
            if (role === 'admin') {
                registerLink.classList.add('hidden');
            } else {
                registerLink.classList.remove('hidden');
            }

            // 清空表单
            document.getElementById('loginForm').reset();
        }

        /**
         * 处理登录
         * @param {Event} e - 表单提交事件
         */
        async function handleLogin(e) {
            e.preventDefault();

            const account = document.getElementById('account').value.trim();
            const password = document.getElementById('password').value.trim();

            // 表单验证
            if (!account) {
                alert(currentRole === 'admin' ? '请输入用户名' : '请输入手机号');
                return;
            }
            if (!password) {
                alert('请输入密码');
                return;
            }

            // 构建请求参数
            let apiUrl = '';
            let requestBody = {};

            if (currentRole === 'owner') {
                apiUrl = '${pageContext.request.contextPath}/api/owner/auth?action=login';
                requestBody = { phone: account, password: password };
            } else if (currentRole === 'tenant') {
                apiUrl = '${pageContext.request.contextPath}/api/tenant/auth?action=login';
                requestBody = { phone: account, password: password };
            } else if (currentRole === 'admin') {
                apiUrl = '${pageContext.request.contextPath}/api/admin/auth?action=login';
                requestBody = { username: account, password: password };
            }

            try {
                // 禁用登录按钮
                const loginBtn = document.querySelector('.login-btn');
                loginBtn.disabled = true;
                loginBtn.textContent = '登录中...';

                const response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestBody)
                });

                const result = await response.json();

                if (result.code === 200) {
                    // 保存token和用户信息到localStorage
                    localStorage.setItem('token', result.data.token);
                    localStorage.setItem('userInfo', JSON.stringify(result.data.userInfo || result.data));
                    localStorage.setItem('role', currentRole);

                    // 根据角色跳转到对应的dashboard
                    let redirectUrl = '';
                    if (currentRole === 'owner') {
                        redirectUrl = '${pageContext.request.contextPath}/owner/dashboard.jsp';
                    } else if (currentRole === 'tenant') {
                        redirectUrl = '${pageContext.request.contextPath}/tenant/dashboard.jsp';
                    } else if (currentRole === 'admin') {
                        redirectUrl = '${pageContext.request.contextPath}/admin/dashboard.jsp';
                    }

                    window.location.href = redirectUrl;
                } else {
                    // 显示错误信息
                    alert(result.message || '登录失败，请重试');
                    loginBtn.disabled = false;
                    loginBtn.textContent = '登 录';
                }
            } catch (error) {
                console.error('登录请求失败:', error);
                alert('网络错误，请稍后重试');
                const loginBtn = document.querySelector('.login-btn');
                loginBtn.disabled = false;
                loginBtn.textContent = '登 录';
            }
        }
    </script>
</body>
</html>
