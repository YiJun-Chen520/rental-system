<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人信息 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <h2>个人信息</h2>
            <div class="profile-section">
                <h3>基本信息</h3>
                <form id="profileForm" onsubmit="saveProfile(event)">
                    <div class="form-group">
                        <label>姓名：</label>
                        <input type="text" id="name" placeholder="请输入姓名">
                    </div>
                    <div class="form-group">
                        <label>手机号：</label>
                        <input type="text" id="phone" placeholder="请输入手机号">
                    </div>
                    <div class="form-group">
                        <label>身份证号：</label>
                        <input type="text" id="idCard" placeholder="请输入身份证号">
                    </div>
                    <div class="form-group">
                        <label>工作单位：</label>
                        <input type="text" id="workUnit" placeholder="请输入工作单位">
                    </div>
                    <div class="form-group">
                        <button type="submit">保存信息</button>
                    </div>
                </form>
            </div>
            <div class="profile-section">
                <h3>修改密码</h3>
                <form id="passwordForm" onsubmit="changePassword(event)">
                    <div class="form-group">
                        <label>原密码：</label>
                        <input type="password" id="oldPassword" placeholder="请输入原密码">
                    </div>
                    <div class="form-group">
                        <label>新密码：</label>
                        <input type="password" id="newPassword" placeholder="请输入新密码">
                    </div>
                    <div class="form-group">
                        <label>确认新密码：</label>
                        <input type="password" id="confirmPassword" placeholder="请再次输入新密码">
                    </div>
                    <div class="form-group">
                        <button type="submit">修改密码</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            loadProfile();
        });

        function loadProfile() {
            var token = localStorage.getItem('token');
            fetch('${pageContext.request.contextPath}/api/tenant/auth?action=info', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    var data = result.data;
                    document.getElementById('name').value = data.name || '';
                    document.getElementById('phone').value = data.phone || '';
                    document.getElementById('idCard').value = data.idCard || '';
                    document.getElementById('workUnit').value = data.workUnit || '';
                } else {
                    alert(result.message || '获取个人信息失败');
                }
            })
            .catch(function(error) {
                console.error('加载个人信息失败:', error);
            });
        }

        function saveProfile(event) {
            event.preventDefault();
            var token = localStorage.getItem('token');
            var data = {
                name: document.getElementById('name').value,
                phone: document.getElementById('phone').value,
                idCard: document.getElementById('idCard').value,
                workUnit: document.getElementById('workUnit').value
            };

            if (!data.name) {
                alert('请输入姓名');
                return;
            }
            if (!data.phone) {
                alert('请输入手机号');
                return;
            }

            fetch('${pageContext.request.contextPath}/api/tenant/auth?action=update', {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('个人信息更新成功！');
                } else {
                    alert(result.message || '更新失败');
                }
            })
            .catch(function(error) {
                console.error('更新个人信息失败:', error);
                alert('更新失败，请稍后重试');
            });
        }

        function changePassword(event) {
            event.preventDefault();
            var oldPassword = document.getElementById('oldPassword').value;
            var newPassword = document.getElementById('newPassword').value;
            var confirmPassword = document.getElementById('confirmPassword').value;

            if (!oldPassword) {
                alert('请输入原密码');
                return;
            }
            if (!newPassword) {
                alert('请输入新密码');
                return;
            }
            if (newPassword !== confirmPassword) {
                alert('两次输入的新密码不一致');
                return;
            }
            if (newPassword.length < 6) {
                alert('新密码长度不能少于6位');
                return;
            }

            var token = localStorage.getItem('token');
            fetch('${pageContext.request.contextPath}/api/tenant/auth?action=changePassword', {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    oldPassword: oldPassword,
                    newPassword: newPassword
                })
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    alert('密码修改成功，请重新登录');
                    localStorage.removeItem('token');
                    window.location.href = '${pageContext.request.contextPath}/login.jsp';
                } else {
                    alert(result.message || '密码修改失败');
                }
            })
            .catch(function(error) {
                console.error('修改密码失败:', error);
                alert('密码修改失败，请稍后重试');
            });
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
