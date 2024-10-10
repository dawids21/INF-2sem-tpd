package xyz.stasiak.beanssoapservice.types;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BeansSoapCancelRequest {
    private UUID brewId;
}
