package com.storemate.serviceimpl;

import com.google.common.base.Strings;
import com.storemate.JWT.JwtFilter;
import com.storemate.POJO.Category;
import com.storemate.constants.StoreMateConstants;
import com.storemate.dao.CategoryDao;
import com.storemate.service.CategoryService;
import com.storemate.utils.StoreMateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){
                
                if(validateCategorymap(requestMap,false)){
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    return StoreMateUtils.getResponseEntity(requestMap.get("name")+" Category Added Successfully",HttpStatus.OK);
                }

            }else{
                return StoreMateUtils.getResponseEntity(StoreMateConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateCategorymap(Map<String, String> requestMap, boolean validateId) {

        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            } else return !validateId;
        }

        return false;

    }

    private Category getCategoryFromMap(Map<String, String> requestMap,Boolean isAdd){

        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));

        return category;

    }

    //======================================================================================================================================

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside If=================================================================================================");
                return new ResponseEntity<List<Category>>(new ArrayList<Category>(categoryDao.getAllCategory()),HttpStatus.OK);
            }
                return new ResponseEntity<List<Category>>(categoryDao.findAll(),HttpStatus.OK);

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //=================================================================================================================================================

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){
                if(validateCategorymap(requestMap,true)){
                  Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                  if(!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return StoreMateUtils.getResponseEntity(requestMap.get("name")+" Category Updated Successfully",HttpStatus.OK);
                  }else {
                      return StoreMateUtils.getResponseEntity("Category Id Does Not Exist",HttpStatus.OK);
                  }
                }
                return StoreMateUtils.getResponseEntity("Invalid Data",HttpStatus.OK);
            }else{
                return StoreMateUtils.getResponseEntity(StoreMateConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StoreMateUtils.getResponseEntity(StoreMateConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
