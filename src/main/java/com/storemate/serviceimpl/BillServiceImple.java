package com.storemate.serviceimpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemate.JWT.JwtUtil;
import com.storemate.JWT.JwtFilter;
import com.storemate.POJO.Bill;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.BillDao;
import com.storemate.service.BillService;
import com.storemate.utils.StoreMateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImple implements BillService {

    @Autowired
    private BillDao billDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateBill(Map<String, String> requestMap) {
        try {
            String fileName;
            if (validateRequestMap(requestMap)) {
                // Create directory if it doesn't exist
                File fileDir = new File(StoreMateConstants.STORE_LOCATION);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();  // This creates the directory if it does not exist
                }

                if (requestMap.containsKey("isGenerate") && requestMap.get("isGenerate").equals("true")) {
                    fileName = requestMap.get("uuid"); // Set file name as customer name
                } else {
                    fileName = StoreMateUtils.getUuid();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                // Data For Information
                String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " + requestMap.get("email") + "\n" + "Payment Method: " + requestMap.get("paymentMethod");

                // Generating PDF
                String filePath = StoreMateConstants.STORE_LOCATION + File.separator + fileName + ".pdf";
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));

                document.open();
                setBorderInPdf(document);

                // Adding Heading with custom color
                Paragraph heading = new Paragraph("Store Mate Pro", getFontM("Header"));
                heading.setAlignment(Element.ALIGN_CENTER);
                heading.setFont(new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE)); // Blue heading
                document.add(heading);

                // Adding Customer Data
                Paragraph paragraph = new Paragraph(data + "\n\n", getFontM("Data"));
                paragraph.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraph);

                // Adjusting margins for the table to fit within borders
                document.setMargins(10, 10, 50, 50);

                // Creating Product Table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3, 3, 2, 2, 2}); // Adjust the width ratios for columns
                table.setSpacingBefore(20f); // Add space before table
                addTableHeader(table);

                // Adding Product Details Into Table
                JSONArray jsonArr = StoreMateUtils.getJsonArrFromString(requestMap.get("productDetails"));
                for (int i = 0; i < jsonArr.length(); i++) {
                    addRow(table, StoreMateUtils.getMapFromJson(jsonArr.getString(i)));
                }
                document.add(table);

                // Adding Footer with centered total and highlighted
                Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n" + "Thank You For Visiting", getFontM("Total"));
                footer.setAlignment(Element.ALIGN_LEFT); // Center align the total
                footer.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.RED)); // Red for total section
                document.add(footer);

                document.close();

                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            } else {
                return StoreMateUtils.getResponseEntity("Required Data Not Found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    private Font getFontM(String type) {
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            case "Total":
                Font totalFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.RED);
                totalFont.setStyle(Font.BOLD);
                return totalFont;
            default:
                return new Font();
        }
    }

    private boolean validateRequestMap(Map<String, String> requestMap) {
        log.info("Inside validateRequestMap");
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    private void insertBill(Map<String, String> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid(requestMap.get("uuid"));
            bill.setName(requestMap.get("name"));
            bill.setContactNumber(requestMap.get("contactNumber"));
            bill.setEmail(requestMap.get("email"));
            bill.setPaymentMethod(requestMap.get("paymentMethod"));
            bill.setProductDetails(requestMap.get("productDetails"));
            bill.setTotal(Integer.parseInt(requestMap.get("totalAmount")));
            bill.setCreatedBy(jwtFilter.getCurrUserName());
            billDao.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBorderInPdf(Document document) throws DocumentException {
        log.info("Inside Set Border Method");

        // Adjust the right side of the rectangle to make it symmetrical to the left
        Rectangle rectangle = new Rectangle(580, 825, 15, 15); // Reducing the right boundary from 557 to 540

        // Enable borders on all sides (left, right, top, bottom)
        rectangle.enableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);

        // Set the color and width for the border
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);

        // Add the rectangle to the document
        document.add(rectangle);
    }


    private void addTableHeader(PdfPTable table) {
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY); // Light gray background for header
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        });
    }

    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRow");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }
    //===============================================================================================================================================

    @Override
    public ResponseEntity<List<Bill>> getBills() {

            List<Bill>list = new ArrayList<Bill>();
            if(jwtFilter.isAdmin()){
                list = billDao.getAllBills();
            }else{
                list = billDao.getBillByUserName(jwtFilter.getCurrUserName());
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
    }
    //=================================================================================================================================================


    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, String> requestMap) {
        log.info("Inside getPdf");
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }

            String filePath = StoreMateConstants.STORE_LOCATION + File.separator + requestMap.get("uuid") + ".pdf";

            if (StoreMateUtils.isFileExist(filePath)) {
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
                requestMap.put("isGenerate", String.valueOf(false));
                generateBill(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    private byte[] getByteArray(String filePath) throws Exception {

        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;

    }
    //====================================================================================================================================================

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try{
            Optional optional = billDao.findById(id);
            if(optional.isPresent()){
                billDao.deleteById(id);
                return new ResponseEntity<>("Bill deleted Successfully", HttpStatus.OK);
            }
            return StoreMateUtils.getResponseEntity("Bill Id Not Exists", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
