
package com.pps.movie.entity;

import lombok.Data;

/**
 * @author Pu PanSheng, 2021/5/12
 * @version OPRA v1.0
 */
@Data
public class Relation {

    private Integer id;

    private Integer relationId;

    private Integer userId;

    private Integer type;

    private Integer flag;


}
