package com.androsov.itmo_blps_lab1.dto;

import lombok.Data;

import java.util.List;

@Data
public class VacancySearchParams {
    private List<String> keyWords;
    private List<String> cities;
    private Integer salaryFrom;
    private Integer salaryTo;
}
