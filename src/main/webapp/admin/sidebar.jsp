<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar">
    <div class="sidebar-title">房屋租赁系统</div>
    <div class="sidebar-subtitle">管理端</div>
    <nav>
        <a href="dashboard.jsp" class="menu-item">系统概览</a>
        <a href="owner.jsp" class="menu-item">房东管理</a>
        <a href="tenant.jsp" class="menu-item">租户管理</a>
        <a href="house.jsp" class="menu-item">房源管理</a>
        <a href="contract.jsp" class="menu-item">合同管理</a>
        <a href="payment.jsp" class="menu-item">费用管理</a>
        <a href="review.jsp" class="menu-item">评价管理</a>
        <a href="javascript:void(0)" onclick="logout()" class="menu-item">退出登录</a>
    </nav>
</div>

<script>
    /**
     * 退出登录，清除本地存储并跳转到登录页
     */
    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = getContextPath() + '/login.jsp';
    }

    /**
     * 高亮当前页面对应的菜单项
     */
    function highlightCurrentMenu() {
        var currentPage = window.location.pathname.split('/').pop();
        var menuItems = document.querySelectorAll('.sidebar .menu-item');
        menuItems.forEach(function(item) {
            var href = item.getAttribute('href');
            if (href === currentPage) {
                item.classList.add('active');
            }
        });
    }

    /**
     * 获取项目上下文路径
     */
    function getContextPath() {
        var path = window.location.pathname;
        var contextPath = path.substring(0, path.indexOf('/', 1));
        return contextPath || '';
    }

    // 页面加载完成后高亮当前菜单
    document.addEventListener('DOMContentLoaded', highlightCurrentMenu);
</script>
