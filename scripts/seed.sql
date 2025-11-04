-- ===========================================
-- CrewConnect Data Seed (Managers + Employees)
-- mustChangePassword column removed for now
-- ===========================================

DELETE FROM EMPLOYEE;
DELETE FROM MANAGER;

-- =========================
-- Managers (5) - under Admin #1
-- =========================
INSERT INTO MANAGER (managerID, firstname, lastname, email, username, password, address, phonenumber, admin_adminID)
VALUES
(1, 'Sophia', 'Carter', 'scarter@crewconnect.com', 'scarter', 'Manager@123', '200 Park Ave, Minneapolis, MN', '612-555-2001', 1),
(2, 'Liam', 'Nguyen', 'lnguyen@crewconnect.com', 'lnguyen', 'Manager@123', '201 Park Ave, Minneapolis, MN', '612-555-2002', 1),
(3, 'Olivia', 'Hughes', 'ohughes@crewconnect.com', 'ohughes', 'Manager@123', '202 Park Ave, Minneapolis, MN', '612-555-2003', 1),
(4, 'Noah', 'Foster', 'nfoster@crewconnect.com', 'nfoster', 'Manager@123', '203 Park Ave, Minneapolis, MN', '612-555-2004', 1),
(5, 'Isabella', 'Brooks', 'ibrooks', 'ibrooks@crewconnect.com', 'Manager@123', '204 Park Ave, Minneapolis, MN', '612-555-2005', 1);

-- =========================
-- Employees (45)
-- Distributed across 5 managers
-- =========================
INSERT INTO EMPLOYEE (employeeID, firstname, lastname, email, username, password, address, phonenumber, manager_managerID)
VALUES
-- Manager 1
(1, 'Ethan', 'Ward', 'eward@crewconnect.com', 'eward', 'Employee@123', '300 Elm St, Minneapolis, MN', '612-555-3001', 1),
(2, 'Mia', 'Reed', 'mreed@crewconnect.com', 'mreed', 'Employee@123', '301 Elm St, Minneapolis, MN', '612-555-3002', 1),
(3, 'Henry', 'Parker', 'hparker@crewconnect.com', 'hparker', 'Employee@123', '302 Elm St, Minneapolis, MN', '612-555-3003', 1),
(4, 'Aria', 'Cook', 'acook@crewconnect.com', 'acook', 'Employee@123', '303 Elm St, Minneapolis, MN', '612-555-3004', 1),
(5, 'Jack', 'Bailey', 'jbailey@crewconnect.com', 'jbailey', 'Employee@123', '304 Elm St, Minneapolis, MN', '612-555-3005', 1),
(6, 'Chloe', 'Powell', 'cpowell@crewconnect.com', 'cpowell', 'Employee@123', '305 Elm St, Minneapolis, MN', '612-555-3006', 1),
(7, 'Aiden', 'Perry', 'aperry@crewconnect.com', 'aperry', 'Employee@123', '306 Elm St, Minneapolis, MN', '612-555-3007', 1),
(8, 'Oliver', 'Diaz', 'odiaz@crewconnect.com', 'odiaz', 'Employee@123', '307 Elm St, Minneapolis, MN', '612-555-3008', 1),
(9, 'Ella', 'Gray', 'egray@crewconnect.com', 'egray', 'Employee@123', '308 Elm St, Minneapolis, MN', '612-555-3009', 1),

-- Manager 2
(10, 'James', 'Rivera', 'jrivera@crewconnect.com', 'jrivera', 'Employee@123', '309 Pine St, Minneapolis, MN', '612-555-3010', 2),
(11, 'Ava', 'Peterson', 'apeterson@crewconnect.com', 'apeterson', 'Employee@123', '310 Pine St, Minneapolis, MN', '612-555-3011', 2),
(12, 'Ella', 'Long', 'elong@crewconnect.com', 'elong', 'Employee@123', '311 Pine St, Minneapolis, MN', '612-555-3012', 2),
(13, 'Carter', 'Stewart', 'cstewart@crewconnect.com', 'cstewart', 'Employee@123', '312 Pine St, Minneapolis, MN', '612-555-3013', 2),
(14, 'Scarlett', 'Hayes', 'shayes@crewconnect.com', 'shayes', 'Employee@123', '313 Pine St, Minneapolis, MN', '612-555-3014', 2),
(15, 'Michael', 'Green', 'mgreen@crewconnect.com', 'mgreen', 'Employee@123', '314 Pine St, Minneapolis, MN', '612-555-3015', 2),
(16, 'Zoe', 'James', 'zjames@crewconnect.com', 'zjames', 'Employee@123', '315 Pine St, Minneapolis, MN', '612-555-3016', 2),
(17, 'Evelyn', 'Hall', 'ehall@crewconnect.com', 'ehall', 'Employee@123', '316 Pine St, Minneapolis, MN', '612-555-3017', 2),
(18, 'Scarlet', 'Young', 'syoung@crewconnect.com', 'syoung', 'Employee@123', '317 Pine St, Minneapolis, MN', '612-555-3018', 2),

-- Manager 3
(19, 'Logan', 'Morris', 'lmorris@crewconnect.com', 'lmorris', 'Employee@123', '400 Oak St, Minneapolis, MN', '612-555-3019', 3),
(20, 'Grace', 'Rogers', 'grogers@crewconnect.com', 'grogers', 'Employee@123', '401 Oak St, Minneapolis, MN', '612-555-3020', 3),
(21, 'Layla', 'Howard', 'lhoward@crewconnect.com', 'lhoward', 'Employee@123', '402 Oak St, Minneapolis, MN', '612-555-3021', 3),
(22, 'Nathan', 'Evans', 'nevans@crewconnect.com', 'nevans', 'Employee@123', '403 Oak St, Minneapolis, MN', '612-555-3022', 3),
(23, 'Avery', 'Bell', 'abell@crewconnect.com', 'abell', 'Employee@123', '404 Oak St, Minneapolis, MN', '612-555-3023', 3),
(24, 'Daniel', 'Adams', 'dadams@crewconnect.com', 'dadams', 'Employee@123', '405 Oak St, Minneapolis, MN', '612-555-3024', 3),
(25, 'Lily', 'Jenkins', 'ljenkins@crewconnect.com', 'ljenkins', 'Employee@123', '406 Oak St, Minneapolis, MN', '612-555-3025', 3),
(26, 'Sebastian', 'Barnes', 'sbarnes@crewconnect.com', 'sbarnes', 'Employee@123', '407 Oak St, Minneapolis, MN', '612-555-3026', 3),
(27, 'Mason', 'Phillips', 'mphillips@crewconnect.com', 'mphillips', 'Employee@123', '408 Oak St, Minneapolis, MN', '612-555-3027', 3),

-- Manager 4
(28, 'Lucas', 'Bennett', 'lbennett@crewconnect.com', 'lbennett', 'Employee@123', '500 Birch St, Minneapolis, MN', '612-555-3028', 4),
(29, 'Harper', 'Price', 'hprice@crewconnect.com', 'hprice', 'Employee@123', '501 Birch St, Minneapolis, MN', '612-555-3029', 4),
(30, 'Samuel', 'Russell', 'srussell@crewconnect.com', 'srussell', 'Employee@123', '502 Birch St, Minneapolis, MN', '612-555-3030', 4),
(31, 'Victoria', 'Fisher', 'vfisher@crewconnect.com', 'vfisher', 'Employee@123', '503 Birch St, Minneapolis, MN', '612-555-3031', 4),
(32, 'Dylan', 'Henderson', 'dhenderson@crewconnect.com', 'dhenderson', 'Employee@123', '504 Birch St, Minneapolis, MN', '612-555-3032', 4),
(33, 'Zoey', 'Bryant', 'zbryant@crewconnect.com', 'zbryant', 'Employee@123', '505 Birch St, Minneapolis, MN', '612-555-3033', 4),
(34, 'Leo', 'Simmons', 'lsimmons@crewconnect.com', 'lsimmons', 'Employee@123', '506 Birch St, Minneapolis, MN', '612-555-3034', 4),
(35, 'Aubrey', 'Kelly', 'akelly@crewconnect.com', 'akelly', 'Employee@123', '507 Birch St, Minneapolis, MN', '612-555-3035', 4),
(36, 'Wyatt', 'Clark', 'wclark@crewconnect.com', 'wclark', 'Employee@123', '508 Birch St, Minneapolis, MN', '612-555-3036', 4),

-- Manager 5
(37, 'Benjamin', 'Ross', 'bross@crewconnect.com', 'bross', 'Employee@123', '600 Maple St, Minneapolis, MN', '612-555-3037', 5),
(38, 'Ella', 'Coleman', 'ecoleman@crewconnect.com', 'ecoleman', 'Employee@123', '601 Maple St, Minneapolis, MN', '612-555-3038', 5),
(39, 'Hannah', 'Butler', 'hbutler@crewconnect.com', 'hbutler', 'Employee@123', '602 Maple St, Minneapolis, MN', '612-555-3039', 5),
(40, 'Ryan', 'Turner', 'rturner@crewconnect.com', 'rturner', 'Employee@123', '603 Maple St, Minneapolis, MN', '612-555-3040', 5),
(41, 'Luna', 'Gray', 'lgray@crewconnect.com', 'lgray', 'Employee@123', '604 Maple St, Minneapolis, MN', '612-555-3041', 5),
(42, 'Eli', 'Watson', 'ewatson@crewconnect.com', 'ewatson', 'Employee@123', '605 Maple St, Minneapolis, MN', '612-555-3042', 5),
(43, 'Madison', 'Bryan', 'mbryan@crewconnect.com', 'mbryan', 'Employee@123', '606 Maple St, Minneapolis, MN', '612-555-3043', 5),
(44, 'Matthew', 'Price', 'mprice@crewconnect.com', 'mprice', 'Employee@123', '607 Maple St, Minneapolis, MN', '612-555-3044', 5),
(45, 'Addison', 'Cole', 'acole@crewconnect.com', 'acole', 'Employee@123', '608 Maple St, Minneapolis, MN', '612-555-3045', 5);
