<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 房屋租赁管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        /* 注册页面样式 */
        body {
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            padding: 20px 0;
        }

        .register-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            width: 100%;
            max-width: 480px;
            margin: 20px;
        }

        .register-card h1 {
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

        .form-group label .required {
            color: #e74c3c;
            margin-left: 2px;
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

        /* 注册按钮 */
        .register-btn {
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

        .register-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }

        .register-btn:active {
            transform: translateY(0);
        }

        .register-btn:disabled {
            opacity: 0.7;
            cursor: not-allowed;
            transform: none;
        }

        /* 登录链接 */
        .login-link {
            text-align: center;
            margin-top: 20px;
            font-size: 14px;
            color: #666;
        }

        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        .login-link a:hover {
            text-decoration: underline;
        }

        /* 隐藏元素 */
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <div class="register-card">
        <h1>房屋租赁管理系统</h1>

        <!-- 角色选择标签 -->
        <div class="role-tabs">
            <div class="role-tab active" data-role="owner" onclick="switchRole('owner')">房东注册</div>
            <div class="role-tab" data-role="tenant" onclick="switchRole('tenant')">租户注册</div>
        </div>

        <!-- 注册表单 -->
        <form id="registerForm" onsubmit="handleRegister(event)">
            <!-- 通用字段 -->
            <div class="form-group">
                <label for="name">姓名 <span class="required">*</span></label>
                <input type="text" id="name" name="name" placeholder="请输入真实姓名" required>
            </div>

            <div class="form-group">
                <label for="phone">手机号 <span class="required">*</span></label>
                <input type="tel" id="phone" name="phone" placeholder="请输入手机号" required maxlength="11">
            </div>

            <div class="form-group">
                <label for="idCard">身份证号 <span class="required">*</span></label>
                <input type="text" id="idCard" name="idCard" placeholder="请输入身份证号" required maxlength="18">
            </div>

            <div class="form-group">
                <label for="password">密码 <span class="required">*</span></label>
                <input type="password" id="password" name="password" placeholder="请设置密码（至少6位）" required minlength="6">
            </div>

            <div class="form-group">
                <label for="confirmPassword">确认密码 <span class="required">*</span></label>
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="请再次输入密码" required>
            </div>

            <!-- 房东专用字段：银行账户 -->
            <div class="form-group" id="bankAccountGroup">
                <label for="bankAccount">银行账户 <span class="required">*</span></label>
                <input type="text" id="bankAccount" name="bankAccount" placeholder="请输入银行账户">
            </div>

            <!-- 租户专用字段：工作单位 -->
            <div class="form-group hidden" id="workUnitGroup">
                <label for="workUnit">工作单位</label>
                <input type="text" id="workUnit" name="workUnit" placeholder="请输入工作单位（选填）">
            </div>

            <button type="submit" class="register-btn">注 册</button>
        </form>

        <!-- 登录链接 -->
        <div class="login-link">
            已有账号？<a href="${pageContext.request.contextPath}/login.jsp">返回登录</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        // 当前选中的角色
        let currentRole = 'owner';

        /**
         * 切换角色
         * @param {string} role - 角色类型: owner/tenant
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

            // 显示/隐藏角色特有字段
            const bankAccountGroup = document.getElementById('bankAccountGroup');
            const workUnitGroup = document.getElementById('workUnitGroup');

            if (role === 'owner') {
                bankAccountGroup.classList.remove('hidden');
                workUnitGroup.classList.add('hidden');
                document.getElementById('bankAccount').required = true;
                document.getElementById('workUnit').required = false;
            } else {
                bankAccountGroup.classList.add('hidden');
                workUnitGroup.classList.remove('hidden');
                document.getElementById('bankAccount').required = false;
                document.getElementById('workUnit').required = false;
            }

            // 清空表单
            document.getElementById('registerForm').reset();
        }

        /**
         * 处理注册
         * @param {Event} e - 表单提交事件
         */
        async function handleRegister(e) {
            e.preventDefault();

            // 获取表单值
            const name = document.getElementById('name').value.trim();
            const phone = document.getElementById('phone').value.trim();
            const idCard = document.getElementById('idCard').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            // 表单验证
            if (!name) {
                alert('请输入姓名');
                return;
            }
            if (!phone) {
                alert('请输入手机号');
                return;
            }
            if (!/^1[3-9]\d{9}$/.test(phone)) {
                alert('请输入正确的手机号');
                return;
            }
            if (!idCard) {
                alert('请输入身份证号');
                return;
            }
            if (!/^\d{17}[\dXx]$/.test(idCard)) {
                alert('请输入正确的身份证号');
                return;
            }
            if (!password) {
                alert('请设置密码');
                return;
            }
            if (password.length < 6) {
                alert('密码长度不能少于6位');
                return;
            }
            if (password !== confirmPassword) {
                alert('两次输入的密码不一致');
                return;
            }

            // 构建请求体
            let apiUrl = '';
            let requestBody = {
                name: name,
                phone: phone,
                idCard: idCard,
                password: password
            };

            if (currentRole === 'owner') {
                const bankAccount = document.getElementById('bankAccount').value.trim();
                if (!bankAccount) {
                    alert('请输入银行账户');
                    return;
                }
                requestBody.bankAccount = bankAccount;
                apiUrl = '${pageContext.request.contextPath}/api/owner/auth?action=register';
            } else {
                const workUnit = document.getElementById('workUnit').value.trim();
                requestBody.workUnit = workUnit; // 可选字段
                apiUrl = '${pageContext.request.contextPath}/api/tenant/auth?action=register';
            }

            try {
                // 禁用注册按钮
                const registerBtn = document.querySelector('.register-btn');
                registerBtn.disabled = true;
                registerBtn.textContent = '注册中...';

                const response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestBody)
                });

                const result = await response.json();

                if (result.code === 200) {
                    alert('注册成功，请登录');
                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                } else {
                    // 显示错误信息
                    alert(result.message || '注册失败，请重试');
                    registerBtn.disabled = false;
                    registerBtn.textContent = '注 册';
                }
            } catch (error) {
                console.error('注册请求失败:', error);
                alert('网络错误，请稍后重试');
                const registerBtn = document.querySelector('.register-btn');
                registerBtn.disabled = false;
                registerBtn.textContent = '注 册';
            }
        }
    </script>
</body>
</html>
