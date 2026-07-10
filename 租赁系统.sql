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
-- 初始数据：默认管理员
-- 密码为 BCrypt 加密的 "admin123"
-- =============================================
INSERT INTO Admin (Username, Password) VALUES
('admin', '$2a$10$N.ZOn9MHQb28sYJLBCBP3.Ot1CvnOWJpIbPLmAlMNd2V6H6JhKHfW');
