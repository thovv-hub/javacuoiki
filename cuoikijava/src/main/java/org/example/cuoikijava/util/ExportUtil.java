package org.example.cuoikijava.util;

import org.example.cuoikijava.model.Ticket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportUtil {

    public static void exportTicketToXML(Ticket ticket) {
        String fileName = "HoaDon_Ve_" + System.currentTimeMillis() + ".xml";
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <TicketReceipt>
                <ExportTime>%s</ExportTime>
                <CustomerInfo>
                    <Name>%s</Name>
                    <Phone>%s</Phone>
                </CustomerInfo>
                <TripDetails>
                    <Route>%s</Route>
                    <DepartureTime>%s</DepartureTime>
                    <BusPlate>%s</BusPlate>
                    <SeatNumber>%d</SeatNumber>
                </TripDetails>
                <PaymentInfo>
                    <Method>%s</Method>
                    <Price>%.0f VNĐ</Price>
                    <Status>%s</Status>
                </PaymentInfo>
            </TicketReceipt>
            """.formatted(
                timeNow,
                ticket.getCustomerName(), ticket.getCustomer_phone(),
                ticket.getTrip_name(), ticket.getDeparture_time(),
                ticket.getLicensePlate(), ticket.getSeatNumber(),
                ticket.getPayment_method(), ticket.getTicket_price(), ticket.getPayment_status()
        );

        try (FileWriter writer = new FileWriter(new File(fileName))) {
            writer.write(xmlContent);
            System.out.println("✅ Đã xuất hóa đơn XML thành công: " + fileName);
        } catch (IOException e) {
            System.out.println("❌ Lỗi khi xuất file XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}