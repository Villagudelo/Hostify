package co.edu.uniquindio.application.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Service {

    WIFI("Wifi"),
    PARKING("Parqueadero"),
    POOL("Piscina"),
    BREAKFAST("Desayuno"),
    AIR_CONDITIONING("Aire acondicionado"),
    KITCHEN("Cocina"),
    PETS_ALLOWED("Se permiten mascotas"),
    GYM("Gimnasio"),
    TV("Televisión"),
    HEATING("Calefacción");


    private final String name;
}
