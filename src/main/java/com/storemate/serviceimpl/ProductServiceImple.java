package com.storemate.serviceimpl;

import com.storemate.JWT.JwtFilter;
import com.storemate.POJO.Category;
import com.storemate.POJO.Product;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.ProductDao;
import com.storemate.service.ProductService;
import com.storemate.utils.StoreMateUtils;
import com.storemate.wrapper.ProductWrapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImple implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,false)){
                    productDao.save(getProductFromMap(requestMap,false));
                    return StoreMateUtils.getResponseEntity("Your Product Added SuccessFully", HttpStatus.OK);
                }
                return StoreMateUtils.getResponseEntity(StoreMateConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }

        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdded) {

        Category category = new Category();
        Product product = new Product();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        if (isAdded){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            product.setStatus("true");
        }

        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));

        return product;


    }

    //===================================================================================================================================================

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try{
            return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //================================================================================================================================================

    @Override
    @Transactional  // Ensure transaction management
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, true)) {
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()) {
                        Product product = optional.get();
                        product.setName(requestMap.get("name"));
                        product.setDescription(requestMap.get("description"));
                        product.setPrice(Integer.parseInt(requestMap.get("price")));

                        // Set the category by ID
                        Category category = new Category();
                        category.setId(Integer.parseInt(requestMap.get("categoryId")));
                        product.setCategory(category);

                        // Save the updated product to the database
                        productDao.saveAndFlush(product);  // Save and immediately flush changes to DB
                        return StoreMateUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);
                    } else {
                        return StoreMateUtils.getResponseEntity("Product ID Does Not Exist", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return StoreMateUtils.getResponseEntity(StoreMateConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //=================================================================================================================================================

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Product> optional = productDao.findById(id);
                if (optional.isPresent()) {
                    productDao.deleteById(id);
                    return StoreMateUtils.getResponseEntity("Product Deleted Successfully", HttpStatus.OK);
                }else{
                    return StoreMateUtils.getResponseEntity("Product ID Does Not Exist", HttpStatus.BAD_REQUEST);
                }
            }else{
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //==============================================================================================================================================

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()) {
                    productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return StoreMateUtils.getResponseEntity("Product Status Updated Successfully", HttpStatus.OK);
                }else{
                    return StoreMateUtils.getResponseEntity("Product ID Does Not Exist", HttpStatus.BAD_REQUEST);
                }

            }else{
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //======================================================================================================================================================

    @Override
    public ResponseEntity<List<ProductWrapper>> getProductByCategory(Integer id) {
       try{
           if(jwtFilter.isAdmin()){

               if(productDao.findById(id).isPresent()){
                   return new ResponseEntity<>(productDao.getProductByCategory(id),HttpStatus.OK);
               }else{
                   return new ResponseEntity<>(productDao.getProductByCategory(id),HttpStatus.BAD_REQUEST);
               }

           }else{
               return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
       return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //==============================================================================================================================================

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try{

            if(jwtFilter.isAdmin()){
                if(productDao.findById(id).isPresent()){
                    return new ResponseEntity<ProductWrapper>(productDao.getProductById(id),HttpStatus.OK);
                }else{
                    return new ResponseEntity<ProductWrapper>(productDao.getProductById(id),HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(new ProductWrapper(), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
