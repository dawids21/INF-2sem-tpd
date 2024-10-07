/*
 * Travel mircro service
 * Micro service to book a travel
 *
 * OpenAPI spec version: 1.0.0
 * Contact: supportm@bp.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package org.bp.travel.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.bp.travel.model.Hotel;
import org.bp.travel.model.Person;
/**
 * BookRoomRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-10-07T13:11:21.681+02:00[Europe/Belgrade]")
public class BookRoomRequest {
  @JsonProperty("person")
  private Person person = null;

  @JsonProperty("hotel")
  private Hotel hotel = null;

  public BookRoomRequest person(Person person) {
    this.person = person;
    return this;
  }

   /**
   * Get person
   * @return person
  **/
  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public BookRoomRequest hotel(Hotel hotel) {
    this.hotel = hotel;
    return this;
  }

   /**
   * Get hotel
   * @return hotel
  **/
  public Hotel getHotel() {
    return hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookRoomRequest bookRoomRequest = (BookRoomRequest) o;
    return Objects.equals(this.person, bookRoomRequest.person) &&
        Objects.equals(this.hotel, bookRoomRequest.hotel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(person, hotel);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookRoomRequest {\n");
    
    sb.append("    person: ").append(toIndentedString(person)).append("\n");
    sb.append("    hotel: ").append(toIndentedString(hotel)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
