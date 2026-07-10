-- =============================================
-- 房屋租赁管理系统 数据库脚本
-- 版本：v2.0
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- =============================================

CREATE DATABASE IF NOT EXISTS rental_system DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
USE rental_system;

-- =============================================
-- 1. 管理员表
-- =============================================
CREATE TABLE Admin (
    AdminID INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员编号',
    Username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    Password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='管理员表';

-- =============================================
-- 2. 房东信息表
-- =============================================
CREATE TABLE Owner (
    OwnerID INT PRIMARY KEY AUTO_INCREMENT COMMENT '房东编号',
    OwnerName VARCHAR(50) NOT NULL COMMENT '房东姓名',
    Phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    IDCard VARCHAR(18) UNIQUE NOT NULL COMMENT '身份证号',
    BankAccount VARCHAR(30) COMMENT '银行账户',
    Password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    Status TINYINT DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (Phone),
    INDEX idx_status (Status)
) COMMENT='房东信息表';

-- =============================================
-- 3. 房源信息表
-- =============================================
CREATE TABLE House (
    HouseID INT PRIMARY KEY AUTO_INCREMENT COMMENT '房源编号',
    OwnerID INT NOT NULL COMMENT '房东编号',
    Address VARCHAR(200) NOT NULL COMMENT '房源地址',
    HouseType VARCHAR(20) COMMENT '户型（如：两室一厅）',
    Area DECIMAL(8,2) COMMENT '面积（平方米）',
    MonthlyRent DECIMAL(10,2) NOT NULL COMMENT '月租金（元）',
    Status VARCHAR(10) DEFAULT '空闲' COMMENT '房源状态：空闲/已租/已下架',
    Facilities TEXT COMMENT '配套设施',
    Description TEXT COMMENT '房源描述',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (OwnerID) REFERENCES Owner(OwnerID),
    INDEX idx_owner (OwnerID),
    INDEX idx_status (Status),
    INDEX idx_rent (MonthlyRent),
    INDEX idx_address (Address(50))
) COMMENT='房源信息表';

-- =============================================
-- 4. 租户信息表
-- =============================================
CREATE TABLE Tenant (
    TenantID INT PRIMARY KEY AUTO_INCREMENT COMMENT '租户编号',
    TenantName VARCHAR(50) NOT NULL COMMENT '租户姓名',
    Phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    IDCard VARCHAR(18) UNIQUE NOT NULL COMMENT '身份证号',
    Workplace VARCHAR(100) COMMENT '工作单位',
    Password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    Status TINYINT DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (Phone),
    INDEX idx_status (Status)
) COMMENT='租户信息表';

-- =============================================
-- 5. 租赁合同表
-- =============================================
CREATE TABLE Contract (
    ContractID INT PRIMARY KEY AUTO_INCREMENT COMMENT '合同编号',
    HouseID INT NOT NULL COMMENT '房源编号',
    TenantID INT NOT NULL COMMENT '租户编号',
    SignDate DATE NOT NULL COMMENT '签订日期',
    StartDate DATE NOT NULL COMMENT '起始日期',
    EndDate DATE NOT NULL COMMENT '终止日期',
    MonthlyRent DECIMAL(10,2) NOT NULL COMMENT '月租金（元）',
    Deposit DECIMAL(10,2) COMMENT '押金金额（元）',
    Status VARCHAR(10) DEFAULT '生效' COMMENT '合同状态：生效/已到期/已终止',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (HouseID) REFERENCES House(HouseID),
    FOREIGN KEY (TenantID) REFERENCES Tenant(TenantID),
    INDEX idx_house (HouseID),
    INDEX idx_tenant (TenantID),
    INDEX idx_status (Status),
    INDEX idx_date (StartDate, EndDate)
) COMMENT='租赁合同表';

-- =============================================
-- 6. 费用缴纳表
-- =============================================
CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT COMMENT '费用编号',
    ContractID INT NOT NULL COMMENT '合同编号',
    PaymentType VARCHAR(20) NOT NULL COMMENT '费用类型：租金/押金/水费/电费/物业费',
    Amount DECIMAL(10,2) NOT NULL COMMENT '金额（元）',
    PayDate DATE COMMENT '缴费日期',
    PayStatus VARCHAR(10) DEFAULT '未缴' COMMENT '缴费状态：未缴/已缴',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (ContractID) REFERENCES Contract(ContractID),
    INDEX idx_contract (ContractID),
    INDEX idx_status (PayStatus),
    INDEX idx_type (PaymentType)
) COMMENT='费用缴纳表';

-- =============================================
-- 7. 评价信息表
-- =============================================
CREATE TABLE Review (
    ReviewID INT PRIMARY KEY AUTO_INCREMENT COMMENT '评价编号',
    TenantID INT NOT NULL COMMENT '租户编号',
    HouseID INT NOT NULL COMMENT '房源编号',
    Rating INT NOT NULL COMMENT '评分（1-5分）',
    Content TEXT COMMENT '评价内容',
    ReviewDate DATE COMMENT '评价日期',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (TenantID) REFERENCES Tenant(TenantID),
    FOREIGN KEY (HouseID) REFERENCES House(HouseID),
    CONSTRAINT chk_rating CHECK (Rating BETWEEN 1 AND 5),
    INDEX idx_tenant (TenantID),
    INDEX idx_house (HouseID)
) COMMENT='评价信息表';

-- =============================================
-- 初始数据：默认账号（所有密码均为明文 "123456"）
-- =============================================

-- ========== 管理员 ==========
INSERT INTO Admin (Username, Password) VALUES
('admin', '123456');

-- ========== 房东（4位） ==========
INSERT INTO Owner (OwnerName, Phone, IDCard, BankAccount, Password, Status) VALUES
('张三', '13800000001', '110101199001011234', '6222021234567890001', '123456', 1),
('王五', '13800000002', '110101198805151235', '6222021234567890002', '123456', 1),
('赵六', '13800000003', '110101198603201236', '6222021234567890003', '123456', 1),
('陈七', '13800000004', '110101199207081237', '6222021234567890004', '123456', 1);

-- ========== 租户（6位） ==========
INSERT INTO Tenant (TenantName, Phone, IDCard, Workplace, Password, Status) VALUES
('李四',   '13900000001', '110101199502022345', '北京科技有限公司',     '123456', 1),
('刘八',   '13900000002', '110101199308122346', '腾讯科技（深圳）有限公司', '123456', 1),
('周九',   '13900000003', '110101199106052347', '阿里巴巴集团',       '123456', 1),
('吴十',   '13900000004', '110101199709182348', '字节跳动有限公司',     '123456', 1),
('郑十一', '13900000005', '110101199412032349', '华为技术有限公司',     '123456', 1),
('孙十二', '13900000006', '110101199608152350', '百度在线网络技术公司',   '123456', 1);

-- ========== 房源（10套） ==========
INSERT INTO House (OwnerID, Address, HouseType, Area, MonthlyRent, Status, Facilities, Description) VALUES
-- 张三的房源（3套）
(1, '北京市朝阳区望京SOHO T1-1201',   '两室一厅', 85.50,  8500.00, '已租',  '空调、冰箱、洗衣机、热水器、WiFi', '望京核心地段，交通便利，周边配套齐全'),
(1, '北京市朝阳区望京SOHO T2-805',    '一室一厅', 55.00,  5500.00, '空闲',  '空调、洗衣机、热水器、WiFi',     '精装修，拎包入住，适合单身白领'),
(1, '北京市丰台区南四环西路116号-502',  '三室两厅', 120.00, 6800.00, '空闲',  '空调、冰箱、洗衣机、热水器、WiFi、车位', '大三居，适合家庭居住，小区环境优美'),
-- 王五的房源（3套）
(2, '北京市海淀区中关村大街88号-1502', '两室一厅', 78.00,  9200.00, '已租',  '空调、冰箱、洗衣机、热水器、WiFi', '中关村学区房，靠近地铁站'),
(2, '北京市海淀区学院路15号-603',     '一室一厅', 48.00,  6000.00, '空闲',  '空调、洗衣机、热水器',         '学院路高校区，交通方便'),
(2, '北京市通州区新华大街128号-1801',  '三室一厅', 95.00,  5200.00, '空闲',  '空调、冰箱、洗衣机、热水器、WiFi', '通州副中心，新小区，性价比高'),
-- 赵六的房源（2套）
(3, '北京市西城区金融街19号-2201',    '两室两厅', 92.00, 12000.00, '已租',  '空调、冰箱、洗衣机、烘干机、热水器、WiFi', '金融街核心位置，高端公寓'),
(3, '北京市西城区西单北大街110号-302', '一室一厅', 42.00,  5800.00, '已下架', '空调、洗衣机、热水器',         '西单商圈，生活便利'),
-- 陈七的房源（2套）
(4, '北京市东城区东直门外大街48号-1105', '两室一厅', 80.00, 7800.00, '已租',  '空调、冰箱、洗衣机、热水器、WiFi', '东直门交通枢纽，出行方便'),
(4, '北京市东城区建国门内大街18号-901',  '一室一厅', 50.00, 6500.00, '空闲',  '空调、洗衣机、热水器、WiFi',     '建国门CBD区域，商业氛围浓厚');

-- ========== 合同（6份） ==========
INSERT INTO Contract (HouseID, TenantID, SignDate, StartDate, EndDate, MonthlyRent, Deposit, Status) VALUES
(1,  1, '2025-06-15', '2025-07-01', '2026-06-30',  8500.00, 17000.00, '生效'),   -- 李四租张三的房子
(4,  2, '2025-08-20', '2025-09-01', '2026-08-31',  9200.00, 18400.00, '生效'),   -- 刘八租王五的房子
(7,  3, '2025-03-10', '2025-04-01', '2026-03-31', 12000.00, 24000.00, '生效'),   -- 周九租赵六的房子
(9,  4, '2024-11-05', '2024-12-01', '2025-11-30',  7800.00, 15600.00, '已到期'), -- 吴十租陈七的房子（已到期）
(1,  5, '2024-06-01', '2024-07-01', '2025-06-30',  8500.00, 17000.00, '已终止'), -- 郑十一曾租张三的房子（已终止）
(4,  6, '2025-10-01', '2025-10-15', '2026-10-14',  9200.00, 18400.00, '生效');   -- 孙十二租王五的房子

-- ========== 费用记录（15条） ==========
INSERT INTO Payment (ContractID, PaymentType, Amount, PayDate, PayStatus) VALUES
-- 合同1：李四 - 生效中
(1, '押金', 17000.00, '2025-06-15', '已缴'),
(1, '租金',  8500.00, '2025-07-01', '已缴'),
(1, '租金',  8500.00, '2025-08-01', '已缴'),
(1, '租金',  8500.00, NULL,         '未缴'),  -- 当月未缴
(1, '水费',   156.00, '2025-08-05', '已缴'),
(1, '电费',   238.50, NULL,         '未缴'),
-- 合同2：刘八 - 生效中
(2, '押金', 18400.00, '2025-08-20', '已缴'),
(2, '租金',  9200.00, '2025-09-01', '已缴'),
(2, '租金',  9200.00, NULL,         '未缴'),
-- 合同3：周九 - 生效中
(3, '押金', 24000.00, '2025-03-10', '已缴'),
(3, '租金', 12000.00, '2025-04-01', '已缴'),
(3, '物业费',  960.00, '2025-04-15', '已缴'),
-- 合同4：吴十 - 已到期
(4, '押金', 15600.00, '2024-11-05', '已缴'),
(4, '租金',  7800.00, '2024-12-01', '已缴'),
-- 合同5：郑十一 - 已终止
(5, '押金', 17000.00, '2024-06-01', '已缴');

-- ========== 评价（6条） ==========
INSERT INTO Review (TenantID, HouseID, Rating, Content, ReviewDate) VALUES
(1, 1, 5, '房子非常棒！位置好，装修新，房东人也很 nice。强烈推荐！',         '2025-08-15'),
(2, 4, 4, '整体不错，交通便利，就是楼道有点窄。住着很舒适。',            '2025-10-10'),
(3, 7, 5, '金融街的位置没得说，房子品质很高，配套齐全。就是租金有点贵。',      '2025-06-20'),
(4, 9, 3, '房子还行，但是隔音效果不太好，希望能改善一下。周围生活很方便。',     '2025-03-15'),
(5, 1, 4, '之前租过这套房子，各方面都不错，因为工作调动才搬走的。',          '2025-06-25'),
(6, 4, 5, '刚搬进来不久，非常满意！小区环境好，邻居也很友好。',            '2025-11-20');
