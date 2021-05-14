
package com.pps.movie.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
@Getter
@AllArgsConstructor
public enum RelationType {

    friend(1,"好友");

    private Integer value;

    private String info;



}
