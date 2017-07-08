CREATE TABLE PRODUCT(
	Prodno VARCHAR(10) NOT NULL, 
	Pname VARCHAR(20), 
	Price DECIMAL
   );

CREATE TABLE DEPOT(
	Depno VARCHAR(10) NOT NULL, 
	Addr VARCHAR(20), 
	Volume DECIMAL
   );

CREATE TABLE STOCK(
	Prodno VARCHAR(10) NOT NULL, 
	Depno VARCHAR(10) NOT NULL, 
	Quantity DECIMAL
   );

   
INSERT INTO Product (Prodno, Pname, Price) VALUES ('p1', 'tape', '2.5');
INSERT INTO Product (Prodno, Pname, Price) VALUES ('p2', 'tv', '250');
INSERT INTO Product (Prodno, Pname, Price) VALUES ('p3', 'vcr', '80');

INSERT INTO Depot (Depno, Addr, Volume) VALUES ('d1', 'New York', '9000');
INSERT INTO Depot (Depno, Addr, Volume) VALUES ('d2', 'Syracuse', '6000');
INSERT INTO Depot (Depno, Addr, Volume) VALUES ('d4', 'New York', '2000');


INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p1', 'd1', '1000');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p1', 'd2', '-100');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p1', 'd4', '1200');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p3', 'd1', '3000');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p3', 'd4', '2000');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p2', 'd4', '1500');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p2', 'd1', '-400');
INSERT INTO Stock (Prodno, Depno, Quantity) VALUES ('p2', 'd2', '2000');

ALTER TABLE Product ADD CONSTRAINT pk_Product PRIMARY KEY (Prodno);
ALTER TABLE Product ADD CONSTRAINT ck_Product_Price CHECK (Price > 0);
ALTER TABLE Depot ADD CONSTRAINT pk_Depot PRIMARY KEY (Depno);
ALTER TABLE Stock ADD CONSTRAINT pk_Stock PRIMARY KEY (Prodno,Depno);
ALTER TABLE Stock ADD CONSTRAINT fk_Stock_Prod FOREIGN KEY (Prodno) REFERENCES Product(Prodno);
ALTER TABLE Stock ADD CONSTRAINT fk_Stock_Depot FOREIGN KEY (Depno) REFERENCES Depot(Depno);





