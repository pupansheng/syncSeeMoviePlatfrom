
package com.pps.movie.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
@Getter
@AllArgsConstructor
public enum  FlagType {

    valid(1,"有效"),inValid(0,"无效");

    private Integer value;

    private String info;



}
