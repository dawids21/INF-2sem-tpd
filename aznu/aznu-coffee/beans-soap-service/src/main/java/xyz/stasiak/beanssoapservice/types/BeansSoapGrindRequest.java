package xyz.stasiak.beanssoapservice.types;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BeansSoapGrindRequest {
    private UUID brewId;
    private String name;
}
