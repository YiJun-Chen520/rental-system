/**
 * 房屋租赁管理系统 - 公共JavaScript工具函数
 */

// 获取上下文路径
function getContextPath() {
    var path = window.location.pathname;
    var contextPath = path.substring(0, path.indexOf('/', 1));
    return contextPath || '';
}

/**
 * API请求封装
 * @param {string} url - 请求地址
 * @param {object} options - fetch配置选项
 * @returns {Promise} 返回Promise对象
 */
function request(url, options) {
    options = options || {};
    var headers = Object.assign({
        'Content-Type': 'application/json'
    }, options.headers || {});

    // 添加JWT Token
    var token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    options.headers = headers;

    return fetch(url, options)
        .then(function(res) {
            return res.json();
        })
        .then(function(data) {
            // 未授权，跳转登录页
            if (data.code === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = getContextPath() + '/login.jsp';
                return;
            }
            // 请求失败，显示错误信息
            if (data.code !== 200) {
                alert(data.message || '操作失败');
                throw new Error(data.message || '操作失败');
            }
            return data;
        })
        .catch(function(err) {
            if (err.message !== '操作失败') {
                console.error('请求错误:', err);
            }
            throw err;
        });
}

/**
 * GET请求
 * @param {string} url - 请求地址
 * @returns {Promise}
 */
function apiGet(url) {
    return request(url);
}

/**
 * POST请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @returns {Promise}
 */
function apiPost(url, data) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

/**
 * PUT请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @returns {Promise}
 */
function apiPut(url, data) {
    return request(url, {
        method: 'PUT',
        body: JSON.stringify(data || {})
    });
}

/**
 * DELETE请求
 * @param {string} url - 请求地址
 * @returns {Promise}
 */
function apiDelete(url) {
    return request(url, {
        method: 'DELETE'
    });
}

/**
 * 获取分页参数
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页条数
 * @returns {string} 分页查询字符串
 */
function getPageParams(page, pageSize) {
    return '?page=' + (page || 1) + '&pageSize=' + (pageSize || 10);
}

/**
 * 格式化日期
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期 (YYYY-MM-DD)
 */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    return dateStr.substring(0, 10);
}

/**
 * 格式化金额
 * @param {number} amount - 金额
 * @returns {string} 格式化后的金额
 */
function formatMoney(amount) {
    if (!amount && amount !== 0) return '-';
    return '¥' + parseFloat(amount).toFixed(2);
}

/**
 * 状态标签HTML
 * @param {string} status - 状态文本
 * @returns {string} 包含样式的HTML
 */
function statusBadge(status) {
    var map = {
        '空闲': 'badge-success',
        '已租': 'badge-warning',
        '已下架': 'badge-danger',
        '生效': 'badge-success',
        '已到期': 'badge-warning',
        '已终止': 'badge-danger',
        '未缴': 'badge-danger',
        '已缴': 'badge-success'
    };
    var badgeClass = map[status] || 'badge-info';
    return '<span class="badge ' + badgeClass + '">' + status + '</span>';
}

/**
 * 确认对话框
 * @param {string} message - 确认消息
 * @returns {boolean} 用户是否确认
 */
function confirmAction(message) {
    return confirm(message);
}

/**
 * 渲染分页
 * @param {string} containerId - 容器元素ID
 * @param {number} totalPages - 总页数
 * @param {number} currentPage - 当前页码
 * @param {string} onPageChange - 页码变化时调用的函数名
 */
function renderPagination(containerId, totalPages, currentPage, onPageChange) {
    var container = document.getElementById(containerId);
    if (!container) return;

    var html = '';

    // 上一页按钮
    html += '<button ' + (currentPage <= 1 ? 'disabled' : '') + ' onclick="' + onPageChange + '(' + (currentPage - 1) + ')">上一页</button>';

    // 页码按钮
    for (var i = 1; i <= totalPages; i++) {
        html += '<button class="' + (i === currentPage ? 'active' : '') + '" onclick="' + onPageChange + '(' + i + ')">' + i + '</button>';
    }

    // 下一页按钮
    html += '<button ' + (currentPage >= totalPages ? 'disabled' : '') + ' onclick="' + onPageChange + '(' + (currentPage + 1) + ')">下一页</button>';

    container.innerHTML = html;
}

/**
 * 显示消息提示
 * @param {string} msg - 消息内容
 * @param {string} type - 消息类型 (success/error/warning/info)
 */
function showMessage(msg, type) {
    // 简单实现，可后续优化为更美观的提示
    alert(msg);
}

/**
 * 获取登录用户信息
 * @returns {object|null} 用户信息对象
 */
function getLoginUser() {
    var userStr = localStorage.getItem('user');
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }
    return null;
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
function isLoggedIn() {
    return !!localStorage.getItem('token');
}

/**
 * 退出登录
 */
function logout() {
    localStorage.clear();
    // 直接跳转到根目录下的login.jsp
    window.location.href = '/login.jsp';
}

/**
 * URL参数解析
 * @param {string} name - 参数名
 * @returns {string|null} 参数值
 */
function getUrlParam(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

/**
 * 防抖函数
 * @param {Function} func - 要执行的函数
 * @param {number} wait - 等待时间(毫秒)
 * @returns {Function}
 */
function debounce(func, wait) {
    var timeout;
    return function() {
        var context = this;
        var args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(function() {
            func.apply(context, args);
        }, wait);
    };
}

/**
 * 表单数据转对象
 * @param {HTMLFormElement} form - 表单元素
 * @returns {object} 表单数据对象
 */
function formToObject(form) {
    var formData = new FormData(form);
    var obj = {};
    formData.forEach(function(value, key) {
        obj[key] = value;
    });
    return obj;
}

/**
 * 填充表单数据
 * @param {HTMLFormElement} form - 表单元素
 * @param {object} data - 数据对象
 */
function fillForm(form, data) {
    if (!form || !data) return;
    var elements = form.elements;
    for (var i = 0; i < elements.length; i++) {
        var element = elements[i];
        var name = element.name;
        if (name && data[name] !== undefined) {
            element.value = data[name];
        }
    }
}
