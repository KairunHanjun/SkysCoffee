package org.kelompokwira.wirakopi.wirakopi.Entity;

import java.io.Serializable;

import org.kelompokwira.wirakopi.wirakopi.JsonObject.ConvertGson;
import org.kelompokwira.wirakopi.wirakopi.JsonObject.JsonStuff;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "Id")
@Table(name = "User_Stuff")
public class UserStuff implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "username")
    private User user;
    @Convert(converter = ConvertGson.class)
    @jakarta.persistence.Lob
    @Column(name = "json_content")
    @JsonBackReference
    private JsonStuff jsonContent;
}