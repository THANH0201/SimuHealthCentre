-- Drop the database if it already exists
DROP DATABASE IF EXISTS heathcentre_simu;

-- Create the database
CREATE DATABASE heathcentre_simu;

-- Use the database
USE heathcentre_simu;

-- Create the Configuration table
CREATE TABLE Configuration (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL, -- ''ArrivalTime', 'SimulationTime', 'DelayTime', 'RegisterServicePoint', 'NurseServicePoint', 'GeneralServicePoint', 'SpecialistServicePoint', 'LaboratoryServicePoint', 'RegisterServiceTime', 'NurseServiceTime', 'GeneralServiceTime', 'SpecialistServiceTime', 'LaboratoryServiceTime'
    value INT NOT NULL -- arrival value, simulation value, delay value, number of point (Service), interval value (Service)
);

-- Drop the user account appuser, if it exists
--DROP USER IF EXISTS 'appuser'@'localhost';

-- Create the user account appuser
--CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'password';

-- Grant privileges to appuser
GRANT SELECT, INSERT, UPDATE, DELETE ON heathcentre_simu.* TO 'appuser'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;
