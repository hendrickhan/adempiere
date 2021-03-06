CREATE OR REPLACE VIEW RV_WM_InOutBoundDetail AS
SELECT 
iob.DocStatus, 
iob.IsApproved, 
iob.Created, 
iob.Updated, 
iob.IsActive, 
iob.DocumentNo,  
iob.DatePrinted, 
iob.DeliveryRule,
iob.TrackingNo, 
iob.Volume, 
iob.Weight, 
iob.DateTrx, 
iob.IsInTransit, 
iob.DeliveryViaRule, 
iob.DropShip_BPartner_ID, 
iob.DropShip_Location_ID, 
iob.DropShip_User_ID, 
iob.FreightCostRule, 
iob.IsDropShip, 
iob.IsPrinted, 
iob.IsSOTrx, 
iob.POReference,
iob.PriorityRule, 
iob.SendEMail,
iob.AD_Org_ID, 
iob.C_DocType_ID, 
iob.AD_Client_ID, 
iob.WM_InOutBound_ID, 
iob.SalesRep_ID,  
iob.UpdatedBy, 
iob.M_Warehouse_ID, 
iob.CreatedBy,
iobl.C_ProjectPhase_ID, 
iobl.PickedQty, 
iobl.C_ProjectTask_ID, 
iobl.User1_ID, 
iobl.User2_ID, 
iobl.PickDate, 
iobl.ShipDate, 
iobl.MovementQty, 
iobl.PP_MRP_ID, 
iobl.PP_Order_BOMLine_ID, 
iobl.PP_Order_ID, 
iobl.FreightAmt, 
iobl.AD_OrgTrx_ID, 
iobl.C_Activity_ID, 
iobl.C_Campaign_ID, 
iobl.C_Charge_ID, 
iobl.C_Project_ID, 
iobl.Description, 
iobl.IsDescription, 
iobl.Line, 
iobl.WM_InOutBoundLine_ID, 
iobl.DD_OrderLine_ID, 
iobl.DD_Order_ID, 
iobl.C_Order_ID, 
iobl.M_LocatorTo_ID, 
iobl.M_Locator_ID, 
iobl.C_OrderLine_ID, 
iobl.C_UOM_ID, 
iobl.M_AttributeSetInstance_ID, 
iobl.M_Product_ID, 
iobl.M_MovementLine_ID, 
iobl.M_Movement_ID, 
iobl.M_InOutLine_ID, 
iobl.M_InOut_ID,
COALESCE(ol.QtyOrdered, dol.QtyOrdered) QtyOrdered,
ol.QtyInvoiced,
COALESCE(ol.QtyDelivered, dol.QtyDelivered) QtyDelivered, 
COALESCE(o.C_BPartner_ID, ddo.C_BPartner_ID) C_BPartner_ID,
COALESCE(o.C_BPartner_Location_ID, ddo.C_BPartner_Location_ID) C_BPartner_Location_ID,
o.Bill_Location_ID,
COALESCE(il.C_Invoice_ID, iobl.C_Invoice_ID) C_Invoice_ID,
COALESCE(il.C_InvoiceLine_ID, iobl.C_InvoiceLine_ID) C_InvoiceLine_ID,
f.DD_Freight_ID,
f.DD_Driver_ID,
f.DD_Vehicle_ID,
f.M_Shipper_ID,
f.M_FreightCategory_ID,
iobl.UUID
FROM WM_InOutBound iob
INNER JOIN WM_InOutBoundLine iobl ON(iobl.WM_InOutBound_ID = iob.WM_InOutBound_ID)
LEFT JOIN C_Order o ON(o.C_Order_ID = iobl.C_Order_ID)
LEFT JOIN C_OrderLine ol ON(ol.C_OrderLine_ID = iobl.C_OrderLine_ID)
LEFT JOIN DD_Order ddo ON(ddo.DD_Order_ID = iobl.DD_Order_ID)
LEFT JOIN DD_OrderLine dol ON(dol.DD_OrderLine_ID = iobl.DD_OrderLine_ID)
LEFT JOIN (SELECT i.C_Invoice_ID, il.C_InvoiceLine_ID, il.WM_InOutBoundLine_ID
            FROM C_Invoice i
            INNER JOIN C_InvoiceLine il ON(il.C_Invoice_ID = i.C_Invoice_ID)
            WHERE i.DocStatus IN('CO', 'CL')) il ON(il.WM_InOutBoundLine_ID = iobl.WM_InOutBoundLine_ID)
LEFT JOIN DD_Freight f ON(f.WM_InOutBound_ID = iob.WM_InOutBound_ID AND f.DocStatus IN('CO', 'CL'));