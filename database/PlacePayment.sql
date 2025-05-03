CREATE PROCEDURE PlacePayment
    @Payment_Method VARCHAR(50),
    @Payment_Status VARCHAR(50),
    @Delivery_ID INT,
    @Restaurant_ID INT
AS
BEGIN
    DECLARE @Payment_ID INT;
    INSERT INTO Payment (Status, Method, Delivery_ID, Restaurant_ID)
    VALUES (@Payment_Status, @Payment_Method, @Delivery_ID, @Restaurant_ID);
    SET @Payment_ID = SCOPE_IDENTITY();
    SELECT @Payment_ID AS PaymentID;
END;
