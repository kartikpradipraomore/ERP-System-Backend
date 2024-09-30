package com.storemate.restimpl;

import com.storemate.POJO.Category;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.CategoryDao;
import com.storemate.rest.CategoryRest;
import com.storemate.service.CategoryService;
import com.storemate.utils.StoreMateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryRestImple implements CategoryRest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ResponseEntity<String> addnewcategory(Map<String, String> requestMap) {
        try{
            return categoryService.addNewCategory(requestMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //===============================================================================================================================================

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            return categoryService.getAllCategory(filterValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//===================================================================================================================================================
    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{

            return categoryService.updateCategory(requestMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
