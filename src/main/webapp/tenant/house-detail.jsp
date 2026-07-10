<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>房源详情 - 租户端</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <div class="page-header">
                <h2>房源详情</h2>
                <a href="house.jsp" class="btn-back">返回列表</a>
            </div>
            <div class="detail-card" id="houseDetail">
                <p>加载中...</p>
            </div>
            <h3>房源评价</h3>
            <div id="reviewList">
                <p>加载中...</p>
            </div>
            <div class="pagination" id="reviewPagination"></div>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script>
        var houseId = new URLSearchParams(window.location.search).get('id');
        var currentPage = 1;
        var pageSize = 10;

        document.addEventListener('DOMContentLoaded', function() {
            if (houseId) {
                loadHouseDetail();
                loadReviews();
            } else {
                document.getElementById('houseDetail').innerHTML = '<p>未指定房源ID</p>';
            }
        });

        function loadHouseDetail() {
            var token = localStorage.getItem('token');
            fetch('${pageContext.request.contextPath}/api/tenant/house?action=detail&id=' + houseId, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    var h = result.data;
                    var html = '<div class="info-grid">';
                    html += '<div class="info-item"><span class="info-label">地址：</span><span>' + (h.address || '') + '</span></div>';
                    html += '<div class="info-item"><span class="info-label">户型：</span><span>' + (h.houseType || '') + '</span></div>';
                    html += '<div class="info-item"><span class="info-label">面积：</span><span>' + (h.area || '') + '㎡</span></div>';
                    html += '<div class="info-item"><span class="info-label">月租金：</span><span>¥' + (h.rent || 0) + '</span></div>';
                    html += '<div class="info-item"><span class="info-label">配套设施：</span><span>' + (h.facilities || '无') + '</span></div>';
                    html += '<div class="info-item"><span class="info-label">房东：</span><span>' + (h.ownerName || '') + '</span></div>';
                    html += '<div class="info-item full-width"><span class="info-label">描述：</span><span>' + (h.description || '暂无描述') + '</span></div>';
                    html += '</div>';
                    document.getElementById('houseDetail').innerHTML = html;
                } else {
                    document.getElementById('houseDetail').innerHTML = '<p>加载失败：' + (result.message || '未知错误') + '</p>';
                }
            })
            .catch(function(error) {
                console.error('加载房源详情失败:', error);
                document.getElementById('houseDetail').innerHTML = '<p>加载失败</p>';
            });
        }

        function loadReviews() {
            var token = localStorage.getItem('token');
            fetch('${pageContext.request.contextPath}/api/tenant/house-review?houseId=' + houseId + '&page=' + currentPage + '&pageSize=' + pageSize, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    var reviews = result.data.list || [];
                    renderReviews(reviews);
                    renderReviewPagination(result.data.total || 0);
                } else {
                    document.getElementById('reviewList').innerHTML = '<p>加载评价失败</p>';
                }
            })
            .catch(function(error) {
                console.error('加载评价失败:', error);
                document.getElementById('reviewList').innerHTML = '<p>加载评价失败</p>';
            });
        }

        function renderReviews(reviews) {
            if (reviews.length === 0) {
                document.getElementById('reviewList').innerHTML = '<p>暂无评价</p>';
                return;
            }
            var html = '';
            for (var i = 0; i < reviews.length; i++) {
                var r = reviews[i];
                html += '<div class="review-item">';
                html += '<div class="review-header">';
                html += '<span class="review-rating">评分: ' + getStars(r.rating) + '</span>';
                html += '<span class="review-author">' + (r.tenantName || '匿名') + '</span>';
                html += '<span class="review-date">' + (r.reviewDate || '') + '</span>';
                html += '</div>';
                html += '<div class="review-content">' + (r.content || '') + '</div>';
                html += '</div>';
            }
            document.getElementById('reviewList').innerHTML = html;
        }

        function getStars(rating) {
            var stars = '';
            for (var i = 0; i < 5; i++) {
                stars += i < rating ? '★' : '☆';
            }
            return stars;
        }

        function renderReviewPagination(total) {
            var totalPages = Math.ceil(total / pageSize);
            var html = '';
            for (var i = 1; i <= totalPages; i++) {
                if (i === currentPage) {
                    html += '<span class="page-btn active">' + i + '</span>';
                } else {
                    html += '<span class="page-btn" onclick="goToReviewPage(' + i + ')">' + i + '</span>';
                }
            }
            document.getElementById('reviewPagination').innerHTML = html;
        }

        function goToReviewPage(page) {
            currentPage = page;
            loadReviews();
        }

        function logout() {
            localStorage.removeItem('token');
            window.location.href = '${pageContext.request.contextPath}/login.jsp';
        }
    </script>
</body>
</html>
