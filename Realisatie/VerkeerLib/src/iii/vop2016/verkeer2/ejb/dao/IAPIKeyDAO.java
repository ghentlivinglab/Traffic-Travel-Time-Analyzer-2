/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.dao;

import iii.vop2016.verkeer2.bean.APIKey.APIKey;
import java.util.List;

/**
 *
 * @author Gebruiker
 */
public interface IAPIKeyDAO {
    
    public boolean validate(String key);
    public void deactivateKey (String key);
    public APIKey getKey();
    public List<APIKey> getAllKeys();
    
}
