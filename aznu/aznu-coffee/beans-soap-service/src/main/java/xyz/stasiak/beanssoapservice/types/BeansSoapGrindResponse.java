package xyz.stasiak.beanssoapservice.types;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BeansSoapGrindResponse {
    private UUID brewId;
    private String name;
    private int weight;
}
