/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;

/**
 *
 * @author Daniel
 */
public class MySecurityManager extends SecurityManager{
    
          public String getCallerClassName(int callStackDepth) {
              return getClassContext()[callStackDepth].getName();
          }
      
}
