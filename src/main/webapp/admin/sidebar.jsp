<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar">
    <div class="sidebar-logo">房屋租赁系统</div>
    <ul class="sidebar-menu">
        <li><a href="dashboard.jsp"><span class="icon">&#128200;</span>系统概览</a></li>
        <li><a href="owner.jsp"><span class="icon">&#127968;</span>房东管理</a></li>
        <li><a href="tenant.jsp"><span class="icon">&#128101;</span>租户管理</a></li>
        <li><a href="house.jsp"><span class="icon">&#127970;</span>房源管理</a></li>
        <li><a href="contract.jsp"><span class="icon">&#128196;</span>合同管理</a></li>
        <li><a href="payment.jsp"><span class="icon">&#128176;</span>费用管理</a></li>
        <li><a href="review.jsp"><span class="icon">&#11088;</span>评价管理</a></li>
        <li><a href="javascript:void(0)" onclick="logout()"><span class="icon">&#128682;</span>退出登录</a></li>
    </ul>
</div>
<script>
    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('role');
        window.location.href = '${pageContext.request.contextPath}/login.jsp';
    }
    document.addEventListener('DOMContentLoaded', function() {
        var currentPage = window.location.pathname.split('/').pop();
        var items = document.querySelectorAll('.sidebar-menu li a');
        items.forEach(function(item) {
            if (item.getAttribute('href') === currentPage) {
                item.parentElement.classList.add('active');
            }
        });
    });
</script>
