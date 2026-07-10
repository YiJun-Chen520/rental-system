<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 公共侧边栏片段 --%>
<div class="sidebar">
    <div class="sidebar-title">房屋租赁系统</div>
    <div class="sidebar-subtitle">租户端</div>
    <nav>
        <a href="dashboard.jsp" class="menu-item">首页概览</a>
        <a href="house.jsp" class="menu-item">浏览房源</a>
        <a href="contract.jsp" class="menu-item">我的合同</a>
        <a href="payment.jsp" class="menu-item">我的费用</a>
        <a href="review.jsp" class="menu-item">我的评价</a>
        <a href="profile.jsp" class="menu-item">个人信息</a>
        <a href="javascript:void(0)" onclick="logout()" class="menu-item logout">退出登录</a>
    </nav>
</div>
