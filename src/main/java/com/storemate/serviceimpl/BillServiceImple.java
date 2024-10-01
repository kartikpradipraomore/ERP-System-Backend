package com.storemate.serviceimpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.storemate.JWT.JwtFilter;
import com.storemate.JWT.JwtUtil;
import com.storemate.POJO.Bill;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.BillDao;
import com.storemate.service.BillService;
import com.storemate.utils.StoreMateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.DocumentFilter;
import java.io.FileOutputStream;
import java.util.Map;

import static com.itextpdf.text.FontFactory.getFont;

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
        try{
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && requestMap.get("isGenerate").equals("true")){
                    fileName = (String) requestMap.get("uuid");
                }else{
                    fileName = StoreMateUtils.getUuid();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }
                String data = "Name: "+ requestMap.get("name")+ "\n" + "Contact Number: "+ requestMap.get("contactNumber")+ "\n" + "Email: "+ requestMap.get("email")+ "\n" + "Payment Method: "+ requestMap.get("paymentMethod");

                Document document = new com.itextpdf.text.Document();
                PdfWriter.getInstance(document, new FileOutputStream(StoreMateConstants.STORE_LOCATION+ "\\" + fileName + ".pdf"));

                document.open();
                setBorderInPdf(document);

                Paragraph heading = new Paragraph("Store Mate Pro",getFontM("Header"));
                heading.setAlignment(Element.ALIGN_CENTER);
                document.add(heading);

            }else{
                return StoreMateUtils.getResponseEntity("Required Data Not Found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Font getFontM(String type) {

        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data" :
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
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
        try{
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
        Rectangle rectangle = new Rectangle(557,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBackgroundColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);

    }

}
