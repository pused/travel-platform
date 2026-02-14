/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : localhost:3306
 Source Schema         : db_travel

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 14/02/2026 23:33:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attraction
-- ----------------------------
DROP TABLE IF EXISTS `attraction`;
CREATE TABLE `attraction`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '景点名称',
  `category_id` int(11) NOT NULL COMMENT '分类ID',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '景点描述',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `rating` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '评分',
  `is_recommend` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_recommend`(`is_recommend`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of attraction
-- ----------------------------
INSERT INTO `attraction` VALUES (1, '黄山风景区', 1, '/file/files/4c4dcbb4d56e4884ba543b3361e7c653.png', '以奇松、怪石、云海、温泉、冬雪\"五绝\"著称于世，被誉为\"天下第一奇山\"。', '安徽省黄山市黄山区', 30.131111, 118.168333, '0559-5561111', 12669, 8900, 4499, 4.8, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (2, '故宫博物院', 2, '/file/files/16a6091c9e764aebb2adfcf6047594c0.png', '中国明清两代的皇家宫殿，是世界上现存规模最大、保存最为完整的木质结构古建筑群之一。', '北京市东城区景山前街4号', 39.916345, 116.397155, '010-85007422', 99216, 75599, 68199, 4.9, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (3, '上海东方明珠', 3, '/file/files/78684d142e8b4de9a74a94de47b69202.png', '上海标志性建筑，集观光、餐饮、购物、娱乐于一体的旅游景点。', '上海市浦东新区世纪大道1号', 31.239692, 121.499740, '021-58791888', 45955, 23400, 12799, 4.5, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (4, '上海迪士尼乐园', 4, '/file/files/1feed344beb44a5f8b72be725d09a6a3.png', '中国内地首座迪士尼主题乐园，拥有七大主题园区，是亲子游玩的理想选择。', '上海市浦东新区川沙新镇', 31.144260, 121.657570, '021-31589999', 68510, 54299, 38900, 4.7, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (5, '中国国家博物馆', 5, '/file/files/6db1bd92f637405aa2077e7dbb41fb0e.png', '世界上建筑面积最大的博物馆，收藏有140万余件藏品，展现中华五千年文明史。', '北京市东城区东长安街16号', 39.904667, 116.401315, '010-65116400', 34200, 18900, 15600, 4.6, 0, '2025-09-25 22:45:56', '2025-09-25 23:03:37');
INSERT INTO `attraction` VALUES (6, '三亚亚龙湾', 6, '/file/files/a95838bc90694b73b0cedfec5dd317d5.png', '被誉为\"天下第一湾\"，拥有7千米长的银白色海滩，沙质细腻，海水清澈见底。', '海南省三亚市吉阳区亚龙湾国家旅游度假区', 18.238000, 109.638000, '0898-88568888', 57067, 43198, 29800, 4.8, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (7, '张家界国家森林公园', 7, '/file/files/8f3160926e9541eeababfdf778bd225a.png', '以独特的石英砂岩峰林地貌著称，是电影《阿凡达》取景地之一。', '湖南省张家界市武陵源区', 29.354600, 110.542800, '0744-5718888', 43558, 32100, 18700, 4.7, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (8, '少林寺', 8, '/file/files/42ca3c4c184a4b71918c6d5b711f34f3.png', '中国佛教禅宗祖庭和少林功夫的发源地，有\"天下第一名刹\"之称。', '河南省登封市嵩山五乳峰下', 34.508000, 112.942000, '0371-62745166', 29800, 18700, 12300, 4.4, 0, '2025-09-25 22:45:56', '2025-09-25 23:04:00');
INSERT INTO `attraction` VALUES (9, '周庄古镇', 9, '/file/files/91d88f12fb9c4d7e8dae81a856a44312.png', '中国第一水乡，保存完好的明清建筑，小桥流水人家的江南水乡风貌。', '江苏省昆山市周庄镇全福路43号', 31.119000, 120.846000, '0512-57211699', 23400, 15600, 9800, 4.5, 0, '2025-09-25 22:45:56', '2025-09-25 23:04:07');
INSERT INTO `attraction` VALUES (10, '广州塔', 10, '/file/files/8cdf12acad634d7699bdd5ca9c11d059.png', '昵称\"小蛮腰\"，中国第一高塔，可俯瞰广州全景的现代化地标建筑。', '广东省广州市海珠区阅江西路222号', 23.119345, 113.323987, '020-89338222', 18702, 12300, 7600, 4.3, 0, '2025-09-25 22:45:56', '2026-01-14 16:09:09');
INSERT INTO `attraction` VALUES (11, '九寨沟国家级自然保护区', 11, '/file/files/79c913a6947547b2bb57068c3d9933bb.png', '以翠海、叠瀑、彩林、雪峰、藏情、蓝冰\"六绝\"著称，被誉为\"童话世界\"。', '四川省阿坝藏族羌族自治州九寨沟县', 33.260000, 103.920000, '0837-7739753', 65758, 49800, 34500, 4.9, 1, '2025-09-25 22:45:56', '2026-02-08 22:02:07');
INSERT INTO `attraction` VALUES (12, '北京动物园', 12, '/file/files/0cad533297084c2b8c2979b677afa5d4.png', '中国开放最早、饲养动物种类最多的动物园，也是中国最大的动物园之一。', '北京市西城区西直门外大街137号', 39.938000, 116.336000, '010-68390274', 15600, 9800, 5400, 4.2, 0, '2025-09-25 22:45:56', '2025-09-25 23:04:29');
INSERT INTO `attraction` VALUES (13, '南京汤山温泉', 13, '/file/files/b79465a238f5458f96c7f66833d84a74.png', '中国四大温泉疗养区之首，水温常年保持在50-60℃，富含多种矿物质。', '江苏省南京市江宁区汤山街道', 32.051000, 119.024000, '025-84107777', 12302, 8700, 4301, 4.4, 0, '2025-09-25 22:45:56', '2025-12-30 01:08:06');
INSERT INTO `attraction` VALUES (14, '婺源油菜花海', 14, '/file/files/2d1ecbdb7dd54e3a896becf05a7969db.png', '中国最美的乡村，春季油菜花盛开时，形成壮观的黄色花海，美不胜收。', '江西省上饶市婺源县', 29.254000, 117.862000, '0793-7343605', 19802, 14500, 8900, 4.6, 1, '2025-09-25 22:45:56', '2025-12-28 14:50:36');
INSERT INTO `attraction` VALUES (15, '青岛啤酒博物馆', 15, '/file/files/cbe90190f4304242a343e5c8971e6d8b.png', '以青岛啤酒百年历史为主题，集文化历史、生产工艺、啤酒娱乐于一体的工业旅游景点。', '山东省青岛市市北区登州路56号', 36.082000, 120.343000, '0532-83833437', 8707, 5398, 3200, 4.3, 0, '2025-09-25 22:45:56', '2025-12-30 01:08:00');
INSERT INTO `attraction` VALUES (16, '西湖风景区', 1, '/file/files/f62a516ef3f74ce1a80acc3bf6e9e7b1.jpg', '中国十大风景名胜之一，以一山、二塔、三岛、三堤、五湖为基本格局。', '浙江省杭州市西湖区龙井路1号', 30.247000, 120.144000, '0571-87179617', 76510, 59799, 43200, 4.8, 1, '2025-09-25 22:45:56', '2026-02-08 22:00:14');
INSERT INTO `attraction` VALUES (17, '秦始皇兵马俑', 2, '/file/files/6dd0a61df73b4b98a1ebc37f4b04753b.png', '世界第八大奇迹，秦始皇陵的陪葬坑，展现秦代军事编制和雕塑艺术的巅峰。', '陕西省西安市临潼区秦陵北路', 34.385000, 109.277000, '029-81399127', 54303, 43200, 29800, 4.9, 1, '2025-09-25 22:45:56', '2026-01-14 16:38:03');
INSERT INTO `attraction` VALUES (18, '香港维多利亚港', 3, '/file/files/5d4e22d52e134d2291abf870949a6635.png', '世界三大天然良港之一，两岸摩天大楼林立，夜景尤为壮观。', '香港特别行政区香港岛和九龙半岛之间', 22.293000, 114.172000, '+852-25081234', 43211, 32100, 18701, 4.7, 1, '2025-09-25 22:45:56', '2026-01-14 16:39:34');
INSERT INTO `attraction` VALUES (19, '峨眉山风景区', 7, '/file/files/5be8399090f34ed8a4b79f1a9ab3599c.png', '中国四大佛教名山之一，以秀丽的自然风光和悠久的佛教文化闻名。', '四川省乐山市峨眉山市名山南路41号', 29.526000, 103.336000, '0833-5533355', 32113, 23400, 15600, 4.6, 0, '2025-09-25 22:45:56', '2026-02-01 18:42:39');
INSERT INTO `attraction` VALUES (20, '乌镇古镇', 9, '/file/files/a3dbd75bf8444c00a9a50986e56b4932.jpg', '典型的江南水乡古镇，完整保存着晚清和民国时期水乡古镇的风貌和格局。', '浙江省嘉兴市桐乡市乌镇石佛南路18号', 30.748000, 120.485000, '0573-88731088', 29818, 18701, 12301, 4.5, 0, '2025-09-25 22:45:56', '2026-02-01 18:06:32');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1142472706 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (8, '桂林山水', '桂林的青山秀水勾勒出一幅灵动的自然画卷，静谧而诗意', '/file/files/9f28baa8ebb34e10bb13cdded4b5d602.jpg', '2025-08-30 08:49:34', '2025-09-27 18:15:10');
INSERT INTO `banner` VALUES (9, '黄山云海', '磅礴的云海在黄山群峰间翻涌，壮阔非凡，宛如置身仙境', '/file/files/cd384bec480e4cccb8d2b534411e4428.png', '2025-09-25 11:00:00', '2025-09-26 22:21:38');
INSERT INTO `banner` VALUES (10, '九寨沟秋色', '深秋的九寨沟，彩林斑斓与澄澈海子相映，宛如一幅绚丽的油画', '/file/files/462ac52229a14219a08e63f019deb4c4.jpg', '2025-09-25 11:05:00', '2025-09-27 18:09:00');
INSERT INTO `banner` VALUES (-1424441342, '稻城亚丁', '稻城亚丁的皑皑雪峰在深邃林海间勾勒出一幅圣洁的极地画卷，冷峻而庄严', '/file/files/27b9f1c24e3344c6ba7cdaf3da9f22f8.jpg', '2025-12-27 20:27:14', '2025-12-27 20:27:14');
INSERT INTO `banner` VALUES (1142472705, '永城', '永城芒砀山的黄瓦红柱勾勒出一抹厚重的汉韵底色，古朴而大气', '/file/files/b04d1a385f044bf1984a121420046bec.jpg', '2025-12-27 20:31:42', '2025-12-27 20:32:03');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '自然风光', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (2, '历史古迹', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (3, '城市地标', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (4, '主题公园', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (5, '博物馆', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (6, '海滩度假', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (7, '山区景点', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (8, '宗教寺庙', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (9, '古镇村落', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (10, '现代建筑', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (11, '国家公园', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (12, '动物园/水族馆', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (13, '温泉养生', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (14, '乡村旅游', '2025-09-25 22:39:19', '2025-09-25 22:39:19');
INSERT INTO `category` VALUES (15, '工业旅游', '2025-09-25 22:39:19', '2025-09-25 22:39:19');

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `target_type` int(11) NOT NULL COMMENT '收藏类型（1-景点，2-攻略）',
  `target_id` int(11) NOT NULL COMMENT '目标ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id`, `target_type`, `target_id`) USING BTREE,
  INDEX `idx_target_type`(`target_type`, `target_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of collect
-- ----------------------------
INSERT INTO `collect` VALUES (2, 6, 1, 16, '2025-09-22 14:20:00');
INSERT INTO `collect` VALUES (3, 6, 2, 1, '2025-09-22 15:05:00');
INSERT INTO `collect` VALUES (4, 7, 1, 1, '2025-09-23 14:30:00');
INSERT INTO `collect` VALUES (5, 7, 1, 7, '2025-09-21 10:35:00');
INSERT INTO `collect` VALUES (6, 7, 1, 11, '2025-09-20 15:50:00');
INSERT INTO `collect` VALUES (10, 9, 1, 2, '2025-09-21 10:25:00');
INSERT INTO `collect` VALUES (11, 9, 1, 17, '2025-09-19 14:45:00');
INSERT INTO `collect` VALUES (12, 10, 1, 3, '2025-09-20 20:40:00');
INSERT INTO `collect` VALUES (13, 10, 1, 6, '2025-09-19 16:30:00');
INSERT INTO `collect` VALUES (14, 11, 1, 4, '2025-09-19 15:50:00');
INSERT INTO `collect` VALUES (15, 12, 1, 6, '2025-09-18 12:35:00');
INSERT INTO `collect` VALUES (16, 12, 2, 3, '2025-09-18 10:20:00');
INSERT INTO `collect` VALUES (17, 4, 1, 19, '2025-09-27 00:13:42');
INSERT INTO `collect` VALUES (26, 8, 1, 20, '2025-12-27 21:12:32');
INSERT INTO `collect` VALUES (27, 8, 1, 13, '2025-12-27 21:12:42');
INSERT INTO `collect` VALUES (29, 8, 1, 18, '2025-12-30 01:20:10');
INSERT INTO `collect` VALUES (30, 8, 2, 4, '2025-12-31 16:04:57');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `strategy_id` int(11) NOT NULL COMMENT '攻略ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '攻略评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 6, '黄山真的太美了！云海奇观让人震撼，特别是日出时分，简直就是仙境。爬山虽然累但很值得，推荐大家一定要去看看！', '2025-09-24 08:30:00', '2025-09-24 08:30:00');
INSERT INTO `comment` VALUES (2, 1, 7, '上次和朋友去的黄山，天气不太好没看到日出有点遗憾。不过迎客松还是很壮观的，下次一定要再去一次！', '2025-09-23 14:20:00', '2025-09-23 14:20:00');
INSERT INTO `comment` VALUES (3, 2, 8, '故宫真的需要一天时间才能看完，建筑太宏伟了！珍宝馆里的文物让人叹为观止，中华文化博大精深。', '2025-09-22 16:45:00', '2025-09-22 16:45:00');
INSERT INTO `comment` VALUES (4, 2, 9, '带孩子去的故宫，小朋友对古建筑很感兴趣。就是人太多了，建议大家避开节假日去。', '2025-09-21 10:15:00', '2025-09-21 10:15:00');
INSERT INTO `comment` VALUES (5, 3, 10, '东方明珠的夜景超级棒！从观光层俯瞰整个上海，灯火辉煌的感觉太震撼了。就是票价有点贵。', '2025-09-20 20:30:00', '2025-09-20 20:30:00');
INSERT INTO `comment` VALUES (6, 4, 11, '迪士尼真的是大朋友小朋友都喜欢的地方！项目很刺激，表演也很精彩。就是要排队时间比较长。', '2025-09-19 15:40:00', '2025-09-19 15:40:00');
INSERT INTO `comment` VALUES (7, 6, 12, '亚龙湾的海水真的超级清澈，沙滩也很细腻。在这里度假真的很放松，海鲜也很新鲜好吃！', '2025-09-18 12:25:00', '2025-09-18 12:25:00');
INSERT INTO `comment` VALUES (8, 7, 6, '张家界的山峰造型太奇特了，感觉像是外星球的景观。天门山的玻璃栈道很刺激，推荐勇敢的朋友去挑战！', '2025-09-17 09:50:00', '2025-09-17 09:50:00');
INSERT INTO `comment` VALUES (9, 11, 7, '九寨沟的水真的是五彩斑斓，每一个海子都有不同的颜色。秋天去最美，红叶配碧水，美得像画一样。', '2025-09-16 14:35:00', '2025-09-16 14:35:00');
INSERT INTO `comment` VALUES (10, 16, 8, '西湖的美是温婉的美，特别是春天的时候，柳絮飞舞，荷花盛开。断桥残雪、雷峰夕照都很有意境。', '2025-09-15 11:20:00', '2025-09-15 11:20:00');
INSERT INTO `comment` VALUES (11, 19, 4, '确实很不错的', '2025-09-27 00:16:17', '2025-09-27 00:16:17');
INSERT INTO `comment` VALUES (12, 5, 4, '不错', '2025-09-27 09:20:04', '2025-09-27 09:20:04');
INSERT INTO `comment` VALUES (13, 5, 4, '哈哈哈哈哈', '2025-09-27 09:31:19', '2025-09-27 09:31:19');
INSERT INTO `comment` VALUES (14, 5, 4, '很好', '2025-09-27 19:19:37', '2025-09-27 19:19:37');
INSERT INTO `comment` VALUES (15, 5, 4, '你好哈哈哈', '2025-09-27 19:58:37', '2025-09-27 19:58:37');
INSERT INTO `comment` VALUES (16, 5, 4, '好', '2025-12-25 21:27:04', '2025-12-25 21:27:04');
INSERT INTO `comment` VALUES (17, 4, 8, '1111', '2025-12-31 16:04:43', '2025-12-31 16:04:43');
INSERT INTO `comment` VALUES (18, 1, 8, '你好', '2026-01-14 16:01:01', '2026-01-14 16:01:01');

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `target_type` int(11) NOT NULL COMMENT '收藏类型（1-景点，2-攻略）',
  `target_id` int(11) NOT NULL COMMENT '目标ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id`, `target_type`, `target_id`) USING BTREE,
  INDEX `idx_target_type`(`target_type`, `target_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of like
-- ----------------------------
INSERT INTO `like` VALUES (1, 6, 1, 1, '2025-09-24 08:35:00');
INSERT INTO `like` VALUES (2, 6, 1, 2, '2025-09-23 16:20:00');
INSERT INTO `like` VALUES (3, 6, 1, 16, '2025-09-22 14:15:00');
INSERT INTO `like` VALUES (4, 7, 1, 1, '2025-09-23 14:25:00');
INSERT INTO `like` VALUES (5, 7, 1, 7, '2025-09-21 10:30:00');
INSERT INTO `like` VALUES (6, 7, 1, 11, '2025-09-20 15:45:00');
INSERT INTO `like` VALUES (8, 8, 1, 3, '2025-09-21 12:20:00');
INSERT INTO `like` VALUES (9, 8, 1, 4, '2025-09-20 18:35:00');
INSERT INTO `like` VALUES (10, 9, 1, 2, '2025-09-21 10:20:00');
INSERT INTO `like` VALUES (11, 9, 1, 17, '2025-09-19 14:40:00');
INSERT INTO `like` VALUES (12, 10, 1, 3, '2025-09-20 20:35:00');
INSERT INTO `like` VALUES (13, 10, 1, 6, '2025-09-19 16:25:00');
INSERT INTO `like` VALUES (14, 11, 1, 4, '2025-09-19 15:45:00');
INSERT INTO `like` VALUES (15, 12, 1, 6, '2025-09-18 12:30:00');
INSERT INTO `like` VALUES (16, 6, 2, 1, '2025-09-22 15:00:00');
INSERT INTO `like` VALUES (17, 7, 2, 1, '2025-09-21 11:30:00');
INSERT INTO `like` VALUES (18, 8, 2, 2, '2025-09-20 17:45:00');
INSERT INTO `like` VALUES (19, 9, 2, 3, '2025-09-19 13:20:00');
INSERT INTO `like` VALUES (20, 10, 2, 2, '2025-09-18 09:15:00');
INSERT INTO `like` VALUES (25, 4, 2, 5, '2025-09-27 19:19:45');
INSERT INTO `like` VALUES (26, 4, 1, 20, '2025-09-27 19:58:12');
INSERT INTO `like` VALUES (27, 4, 2, 1, '2025-12-25 21:28:45');
INSERT INTO `like` VALUES (28, 8, 1, 19, '2026-01-15 10:44:25');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '第一条公告', '欢迎来到系统', 1, '2025-05-04 17:28:53', '2025-05-04 17:28:53');
INSERT INTO `notice` VALUES (2, '国庆假期景点限流通知', '尊敬的用户：\n\n为保障游客安全和游览体验，国庆假期期间各大热门景点将实行限流措施。请提前预约门票，合理安排行程。\n\n限流景点包括：\n- 故宫博物院：每日限流8万人\n- 黄山风景区：每日限流5万人\n- 九寨沟：每日限流4.1万人\n\n感谢您的理解与配合！', 1, '2025-09-25 09:00:00', '2025-09-25 09:00:00');
INSERT INTO `notice` VALUES (3, '新用户注册优惠活动', '新用户注册即可获得100元门票优惠券！\n\n活动时间：2025年9月25日-10月25日\n使用规则：\n1. 仅限新注册用户使用\n2. 单笔订单满300元可用\n3. 不与其他优惠同享\n\n快邀请朋友一起来旅行吧！', 2, '2025-09-25 10:30:00', '2025-09-25 10:30:00');

-- ----------------------------
-- Table structure for strategy
-- ----------------------------
DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `user_id` int(11) NOT NULL COMMENT '作者ID',
  `attraction_id` int(11) NOT NULL COMMENT '景点ID',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片',
  `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '攻略摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '攻略内容',
  `days` int(11) NULL DEFAULT 1 COMMENT '行程天数',
  `budget_type` int(11) NULL DEFAULT 1 COMMENT '预算类型（1-经济，2-舒适，3-豪华）',
  `estimated_budget` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '预估预算',
  `season` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '适合季节',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数',
  `is_recommend` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id`) USING BTREE,
  INDEX `idx_recommend`(`is_recommend`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '旅游攻略表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of strategy
-- ----------------------------
INSERT INTO `strategy` VALUES (1, '黄山三日深度游攻略', 6, 1, '/file/files/a7120a37d6b447ce8f40fd5a0a8db220.png', '详细介绍黄山三日游的最佳路线，包括住宿、交通、景点推荐', '## 黄山三日游完整攻略\n\n### 第一天：抵达黄山\n- 上午：到达黄山北站，乘坐大巴前往黄山风景区\n- 下午：入住山下酒店，休整准备\n- 晚上：品尝徽菜，早休息准备登山\n\n### 第二天：登顶黄山\n- 凌晨4点：起床准备看日出\n- 5点：乘索道上山\n- 6点：到达光明顶等待日出\n- 上午：游览天都峰、莲花峰\n- 下午：游览迎客松、百步云梯\n- 晚上：住山上酒店\n\n### 第三天：下山返程\n- 上午：游览西海大峡谷\n- 下午：乘索道下山，返程', 3, 2, 1200.00, '四季皆宜，春秋最佳', 2850, 153, 89, 1, 1, '2025-09-22 14:30:00', '2026-01-14 16:01:02');
INSERT INTO `strategy` VALUES (2, '故宫半日游精华路线', 7, 2, '/file/files/559d312ea35b4320861afa494dfb91f8.png', '时间有限的朋友必看，用半天时间看遍故宫精华', '## 故宫半日游攻略\n\n### 入园准备\n- 提前网上预约门票\n- 早上8:30准时到达午门\n- 建议租借语音导览\n\n### 推荐路线\n1. 午门 → 太和殿 → 中和殿 → 保和殿\n2. 乾清宫 → 坤宁宫 → 御花园\n3. 珍宝馆（额外收费但值得一看）\n\n### 游览要点\n- 重点参观三大殿和后三宫\n- 珍宝馆的翡翠白菜不容错过\n- 御花园适合拍照留念', 1, 1, 300.00, '四季皆宜', 1940, 234, 66, 0, 1, '2025-09-21 10:15:00', '2025-12-27 19:31:06');
INSERT INTO `strategy` VALUES (3, '上海迪士尼一日游完美攻略', 8, 4, '/file/files/13738677d9ea4d679b6dfa801115b0fb.png', '迪士尼新手必备，教你如何避开人群玩遍所有热门项目', '## 迪士尼一日游攻略\n\n### 开园前准备\n- 提前下载迪士尼官方APP\n- 8:00到达安检口排队\n- 准备好身份证和门票\n\n### 入园后路线\n1. 直奔雷鸣山漂流（获取快速通行证）\n2. 游玩小飞侠天空奇遇\n3. 中午时间游玩室内项目\n4. 下午观看花车巡游\n5. 晚上观看烟花秀\n\n### 省时小贴士\n- 利用快速通行证系统\n- 避开用餐高峰期\n- 提前规划路线', 1, 2, 800.00, '四季皆宜', 3240, 287, 156, 0, 1, '2025-09-20 16:20:00', '2025-09-26 21:29:30');
INSERT INTO `strategy` VALUES (4, '九寨沟秋季摄影攻略', 9, 11, '/file/files/99ea78c77653422396d2713082cbd3f1.png', '专业摄影师分享九寨沟秋季拍摄技巧和最佳机位', '## 九寨沟秋季摄影完全指南\n\n### 最佳拍摄时间\n- 10月中旬至11月初\n- 早晨7-9点（晨雾缭绕）\n- 傍晚5-7点（夕阳西下）\n\n### 必拍景点\n1. 五花海：倒影和彩叶的完美结合\n2. 珍珠滩瀑布：动感水流配红叶\n3. 长海：宁静致远的高原湖泊\n4. 诺日朗瀑布：宽幅瀑布的壮观\n\n### 拍摄技巧\n- 使用偏振镜减少水面反光\n- 长曝光拍摄丝滑水流\n- 黄金时段利用自然光', 2, 2, 1500.00, '秋季', 1680, 198, 135, 1, 0, '2025-09-19 11:45:00', '2025-12-31 16:04:57');
INSERT INTO `strategy` VALUES (5, '三亚亚龙湾度假攻略', 10, 6, '/file/files/ecc466dc9c174485b66032b8d1a52dc2.png', '学生党的三亚穷游指南，低成本享受海岛风情', '## 三亚亚龙湾经济游攻略\n\n### 住宿建议\n- 选择青年旅社或民宿\n- 避开旺季（春节、国庆）\n- 可以住在三亚市区，坐公交到亚龙湾\n\n### 美食推荐\n1. 第一市场：海鲜加工\n2. 当地茶餐厅：椰子鸡\n3. 路边摊：清补凉、椰子\n\n### 免费活动\n- 海滩漫步看日出日落\n- 亚龙湾森林公园徒步\n- 海边游泳和沙滩排球', 4, 1, 600.00, '冬春季节', 980, 76, 22, 5, 0, '2025-09-18 13:25:00', '2025-12-27 19:31:10');

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `attraction_id` int(11) NOT NULL COMMENT '景点ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '门票名称',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片',
  `type` int(11) NULL DEFAULT 1 COMMENT '票种类型（1-成人票，2-儿童票，3-学生票，4-老人票）',
  `price` decimal(10, 2) NOT NULL COMMENT '门票价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门票描述',
  `validity_period` int(11) NULL DEFAULT 1 COMMENT '有效期（天数）',
  `purchase_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '购票须知',
  `inventory` int(11) NULL DEFAULT -1 COMMENT '库存',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '景点门票表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ticket
-- ----------------------------
INSERT INTO `ticket` VALUES (2, 1, '黄山学生票', '/file/files/041d9595a8ba49beb4dec9553ca749e7.jpg', 3, 115.00, 140.00, '凭学生证购买，包含云谷寺索道', 1, '需出示有效学生证；仅限全日制在校学生', 200, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (3, 2, '故宫成人票', '/file/files/080035c979724fa88aed3ae3bbd72008.jpg', 1, 60.00, 60.00, '淡季门票，不含珍宝馆', 1, '需提前网上预约；禁止携带打火机等易燃品', 1000, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (4, 2, '故宫套票', '/file/files/242127f12e0647f4aa964ea0f90575ed.jpg', 1, 80.00, 80.00, '包含珍宝馆和钟表馆', 1, '建议游览时间4小时以上', 800, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (5, 3, '东方明珠观光门票', '/file/files/c92a2342a320437d809e1e22fdc5c70f.jpg', 1, 220.00, 260.00, '含A+B+C三球观光', 1, '身高1米以下儿童免费；1-1.3米儿童半价', 299, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (6, 4, '迪士尼一日票', '/file/files/5c980001437a43deab5cf7d6ae42d7f1.jpg', 1, 399.00, 499.00, '平日票价，含所有游乐设施', 1, '入园需安检；禁止携带食物和饮料', 2000, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (7, 4, '迪士尼儿童票', '/file/files/3d92f5c703284b11a5f430375954b455.jpg', 2, 299.00, 374.00, '身高1-1.4米儿童专享', 1, '需成人陪同入园；验票时需核实身高', 1500, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (8, 6, '亚龙湾沙滩票', '/file/files/042fca57625c4939a3e8c420f2c85052.jpg', 1, 80.00, 100.00, '含沙滩椅和遮阳伞使用', 1, '请注意防晒；水上项目另收费', 399, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (9, 7, '张家界门票', '/file/files/d4034c409ce04230aabe0c9fa5adb8c2.jpg', 1, 248.00, 298.00, '四日内可多次进入，含环保车', 4, '请走指定路线；注意安全', 600, '2025-09-25 10:00:00', '2025-09-25 10:00:00');
INSERT INTO `ticket` VALUES (10, 11, '九寨沟门票', '/file/files/274cbdcca7c84b39829ad7e1a8e82159.jpg', 1, 169.00, 220.00, '淡季价格，含观光车', 30, '请保护环境；不得投喂动物', 797, '2025-09-25 10:00:00', '2025-09-25 10:00:00');

-- ----------------------------
-- Table structure for ticket_order
-- ----------------------------
DROP TABLE IF EXISTS `ticket_order`;
CREATE TABLE `ticket_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单号',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `ticket_id` int(11) NOT NULL COMMENT '门票ID',
  `order_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `pay_num` int(11) NULL DEFAULT NULL COMMENT '下单数量',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总金额',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(1-已付款,2-已完成,3-已取消)',
  `contact_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '联系人手机号',
  `card_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '门票订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ticket_order
-- ----------------------------
INSERT INTO `ticket_order` VALUES (1, 6, 1, 'TO202509240001', 2, 460.00, 1, '李四', '13812345678', '110101199001011234', '2025-09-24 09:00:00', '2025-09-24 09:00:00');
INSERT INTO `ticket_order` VALUES (2, 7, 3, 'TO202509230001', 1, 60.00, 2, '王五', '13987654321', '220102199205155678', '2025-09-23 15:30:00', '2025-09-23 15:30:00');
INSERT INTO `ticket_order` VALUES (3, 8, 5, 'TO202509220001', 3, 660.00, 1, '小丽', '13765432109', '330103199306209012', '2025-09-22 11:45:00', '2025-09-22 11:45:00');
INSERT INTO `ticket_order` VALUES (4, 9, 4, 'TO202509210001', 2, 160.00, 2, '老王', '13654321098', '440104196808113456', '2025-09-21 14:20:00', '2025-09-21 14:20:00');
INSERT INTO `ticket_order` VALUES (5, 10, 6, 'TO202509200001', 1, 399.00, 1, '小小', '13543210987', '510105200201017890', '2025-09-20 16:10:00', '2025-09-20 16:10:00');
INSERT INTO `ticket_order` VALUES (6, 11, 8, 'TO202509190001', 2, 160.00, 2, '大华', '13432109876', '610106198712221234', '2025-09-19 10:25:00', '2025-09-19 10:25:00');
INSERT INTO `ticket_order` VALUES (7, 12, 9, 'TO202509180001', 1, 248.00, 1, '小雨', '13321098765', '710107199004045678', '2025-09-18 13:40:00', '2025-09-18 13:40:00');
INSERT INTO `ticket_order` VALUES (8, 6, 10, 'TO202509170001', 2, 338.00, 2, '李四', '13812345678', '110101199001011234', '2025-09-17 08:55:00', '2025-09-17 08:55:00');
INSERT INTO `ticket_order` VALUES (11, 4, 10, 'TK1758941274867227', 1, 169.00, 2, 'yyu', '19808716541', '221', '2025-09-27 10:47:55', '2025-09-27 10:47:55');
INSERT INTO `ticket_order` VALUES (12, 4, 10, 'TK1758972038834339', 1, 169.00, 3, '张安', '19808716751', '222', '2025-09-27 19:20:38', '2025-09-27 19:20:38');
INSERT INTO `ticket_order` VALUES (13, 4, 10, 'TK1758974353213487', 1, 169.00, 2, '张安', '19876510981', '45132309817810921', '2025-09-27 19:59:13', '2025-09-27 19:59:13');
INSERT INTO `ticket_order` VALUES (14, 4, 8, 'TK1766740103391102', 1, 80.00, 2, '123', '13812345678', '134534523453543453', '2025-12-26 17:08:23', '2025-12-26 17:08:23');
INSERT INTO `ticket_order` VALUES (15, 4, 8, 'TK1766740184518101', 1, 80.00, 1, '1234', '13812345678', '123123421234121111', '2025-12-26 17:09:44', '2025-12-26 17:09:44');
INSERT INTO `ticket_order` VALUES (16, 4, 5, 'TK1766740295544279', 1, 220.00, 1, '2342', '13876709000', '231234513245345345', '2025-12-26 17:11:35', '2025-12-26 17:11:35');
INSERT INTO `ticket_order` VALUES (17, 8, 2, 'TK1767168031669836', 2, 230.00, 3, 'nihao', '13634578990', '123432222222222222', '2025-12-31 16:00:31', '2025-12-31 16:00:31');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nick_name` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `role` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色',
  `age` int(11) NULL DEFAULT 18 COMMENT '年龄',
  `gender` int(11) NULL DEFAULT 2 COMMENT '性别（0-男，1-女，2-保密）',
  `avatar` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `profile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人介绍',
  `status` int(11) NULL DEFAULT 1 COMMENT '账号状态（0-封禁，1-正常）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123', '管理员', 'admin', 18, 2, '/file/files/eb9db3a4019e48f4b8597e4089eb94ab.jpg', '简介很简洁', 1, '2025-09-01 13:52:30', '2025-09-12 23:47:02');
INSERT INTO `user` VALUES (2, 'caiya', '123', '菜牙', 'admin', 18, 2, '/file/files/b6fd112108d24ed1a1649563f8485fa9.jpg', '简介很简洁22', 1, '2025-09-01 13:52:30', '2025-09-27 20:00:07');
INSERT INTO `user` VALUES (4, 'user', '123', '张三', 'user', 25, 0, '/file/files/31ce7826131a426a8a79738ab3446025.jpg', '这是一段个人介绍', 1, '2025-09-12 23:48:32', '2025-09-12 23:50:13');
INSERT INTO `user` VALUES (5, 'zhangsan', '123', '张三', 'user', 25, 0, '/file/files/4738603f598a494dac889b3921dbd5ca.jpg', '这是一段个人介绍', 1, '2025-09-12 23:59:48', '2025-09-13 00:00:28');
INSERT INTO `user` VALUES (6, 'lisi', '123', '李四', 'user', 28, 1, '/file/files/avatar6.jpg', '热爱旅行的摄影师，走遍大江南北', 1, '2025-09-20 10:30:00', '2025-09-20 10:30:00');
INSERT INTO `user` VALUES (7, 'wangwu', '123', '王五', 'user', 32, 0, '/file/files/avatar7.jpg', '户外运动爱好者，喜欢登山徒步', 1, '2025-09-19 14:20:00', '2025-09-19 14:20:00');
INSERT INTO `user` VALUES (8, 'xiaoli', '123', '小丽', 'user', 26, 1, '/file/files/675419b6de8b4e0981977fc2b58520f0.jpg', '美食旅行达人，寻找各地特色小吃', 1, '2025-09-18 16:45:00', '2025-12-27 20:34:26');
INSERT INTO `user` VALUES (9, 'laowang', '123', '老王', 'user', 45, 0, '/file/files/avatar9.jpg', '退休教师，喜欢文化古迹游览', 1, '2025-09-17 09:15:00', '2025-09-17 09:15:00');
INSERT INTO `user` VALUES (10, 'xiaoxiao', '123', '小小', 'user', 22, 2, '/file/files/avatar10.jpg', '在校学生，穷游背包客', 1, '2025-09-16 20:30:00', '2025-09-16 20:30:00');
INSERT INTO `user` VALUES (11, 'dahua', '123', '大华', 'user', 35, 0, '/file/files/avatar11.jpg', '商务人士，偶尔放松度假', 1, '2025-09-15 11:40:00', '2025-09-15 11:40:00');
INSERT INTO `user` VALUES (12, 'xiaoyu', '123', '小雨222', 'user', 29, 1, '/file/files/avatar12.jpg', '自由职业者，随时说走就走的旅行', 1, '2025-09-14 13:25:00', '2025-09-27 20:00:14');
INSERT INTO `user` VALUES (13, 'lihua', '123', 'lihua', NULL, 18, 2, NULL, '这个人很懒，还没有简介', 1, '2026-01-26 15:35:51', '2026-01-26 15:35:51');

SET FOREIGN_KEY_CHECKS = 1;
