package com.example.modiraa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String menu;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    public PostImage(String menu, String imageUrl) {
        this.menu = menu;
        this.imageUrl = imageUrl;
    }
}
