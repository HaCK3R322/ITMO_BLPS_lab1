package com.androsov.itmo_blps.entities.resume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @NotBlank(message = "Education level must not be blank")
    @Pattern(regexp = "^(" +
                "Preschool|" +
                "Initial general|" +
                "Average total|" +
                "Secondary vocational|" +
                "Bachelor's degree|" +
                "specialist's degree, master's degree|" +
                "higher qualification" +
            ")$", message = "Invalid education level value")
    private String level;

    @NotBlank(message = "Education level must not be blank")
    @Pattern(regexp = "^(" +
                "Full-time|" +
                "Part-time|" +
            ")$", message = "Invalid education level value")
    private String form;

    @NotNull(message = "Date of education end is required")
    private LocalDate endDate;

    @NotBlank(message = "University name must not be blank")
    private String universityName;

    @NotBlank(message = "Faculty name must not be blank")
    private String faculty;

    @NotBlank(message = "Specialization name must not be blank")
    private String specialization;

    public Education() {
    }
}