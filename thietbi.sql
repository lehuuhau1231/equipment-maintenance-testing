-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: thietbi
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ho` varchar(20) DEFAULT NULL,
  `ten` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','admin123','Nguyễn','Lâm','lamn9049@gmail.com'),(2,'admin2','admin123','Lê','Hậu',NULL),(3,'admin3','admin123','Trần','Hương',NULL);
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `baotri`
--

DROP TABLE IF EXISTS `baotri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `baotri` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ngayLapLich` datetime NOT NULL,
  `ngayBaoTri` datetime NOT NULL,
  `idThietBi` int NOT NULL,
  `idNhanVien` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `thietbi_baotri_fk_idx` (`idThietBi`),
  KEY `nhanvien_baotri_idx` (`idNhanVien`),
  CONSTRAINT `nhanvien_baotri` FOREIGN KEY (`idNhanVien`) REFERENCES `nhanviensuachua` (`id`),
  CONSTRAINT `thietbi_baotri` FOREIGN KEY (`idThietBi`) REFERENCES `thietbi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `baotri`
--

LOCK TABLES `baotri` WRITE;
/*!40000 ALTER TABLE `baotri` DISABLE KEYS */;
/*!40000 ALTER TABLE `baotri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhanviensuachua`
--

DROP TABLE IF EXISTS `nhanviensuachua`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhanviensuachua` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenNV` varchar(50) NOT NULL,
  `ngaySinh` date NOT NULL,
  `CCCD` char(12) NOT NULL,
  `soDT` char(10) NOT NULL,
  `diaChi` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `idadmin` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `CCCD_UNIQUE` (`CCCD`),
  UNIQUE KEY `soDT_UNIQUE` (`soDT`),
  KEY `admin_NVSuaChua_idx` (`idadmin`),
  CONSTRAINT `admin_NVSuaChua` FOREIGN KEY (`idadmin`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhanviensuachua`
--

LOCK TABLES `nhanviensuachua` WRITE;
/*!40000 ALTER TABLE `nhanviensuachua` DISABLE KEYS */;
INSERT INTO `nhanviensuachua` VALUES (1,'Lâm','2004-07-01','074204006996','0375808832','Quận 12','lamn9049@gmail.com',1),(2,'Hậu','2000-04-23','032203246996','0375808833','Quận 1','email.com',1),(3,'Giang','2002-12-09','074207384927','0375808834','Quận Bình Thạnh','@email.com',1),(4,'Hào','2005-01-08','072938465923','0375808835','Thủ Đức','email@.com',1),(5,'Thanh','1999-03-02','045363829451','0375808836','Nhà Bè','@@@email.com',1);
/*!40000 ALTER TABLE `nhanviensuachua` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhanviensuathietbi`
--

DROP TABLE IF EXISTS `nhanviensuathietbi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhanviensuathietbi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ngaySua` datetime NOT NULL,
  `idThietBi` int NOT NULL,
  `idNhanVien` int NOT NULL,
  `chiPhi` BIGINT DEFAULT NULL,
  `moTa` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nvsuachua_nvsuathietbii_idx` (`idNhanVien`),
  KEY `thietbi_nvsuathietbii_idx` (`idThietBi`),
  CONSTRAINT `nvsuachua_nvsuathietbii` FOREIGN KEY (`idNhanVien`) REFERENCES `nhanviensuachua` (`id`),
  CONSTRAINT `thietbi_nvsuathietbii` FOREIGN KEY (`idThietBi`) REFERENCES `thietbi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhanviensuathietbi`
--

LOCK TABLES `nhanviensuathietbi` WRITE;
/*!40000 ALTER TABLE `nhanviensuathietbi` DISABLE KEYS */;
/*!40000 ALTER TABLE `nhanviensuathietbi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thietbi`
--

DROP TABLE IF EXISTS `thietbi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thietbi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenThietBi` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `thanhLy` date DEFAULT NULL,
  `ngayNhap` date NOT NULL,
  `thongBao` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `idTrangThai` int NOT NULL,
  `idadmin` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `thietbi_admin_fk_idx` (`idadmin`),
  KEY `trangthai_thietbi_idx` (`idTrangThai`),
  CONSTRAINT `thietbi_admin_fk` FOREIGN KEY (`idadmin`) REFERENCES `admin` (`id`),
  CONSTRAINT `trangthai_thietbi_fk` FOREIGN KEY (`idTrangThai`) REFERENCES `trangthai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thietbi`
--

LOCK TABLES `thietbi` WRITE;
/*!40000 ALTER TABLE `thietbi` DISABLE KEYS */;
INSERT INTO `thietbi` VALUES (1,'Quạt điện',NULL,'2025-03-12',3,1),(2,'Máy lạnh',NULL,'2025-03-01',4,1),(3,'Tủ lạnh',NULL,'2025-03-10',4,1),(4,'Bàn ủi',NULL,'2025-03-05',2,1),(5,'Nồi cơm điện','2025-03-23','2025-03-13',1,1);
/*!40000 ALTER TABLE `thietbi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trangthai`
--

DROP TABLE IF EXISTS `trangthai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trangthai` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenTrangThai` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trangthai`
--

LOCK TABLES `trangthai` WRITE;
/*!40000 ALTER TABLE `trangthai` DISABLE KEYS */;
INSERT INTO `trangthai` VALUES (1,'Đã thanh lý'),(2,'Đang hoạt động'),(3,'Đang sửa'),(4,'Hỏng hóc'),(5,'Bảo trì');
/*!40000 ALTER TABLE `trangthai` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-27 15:42:40
