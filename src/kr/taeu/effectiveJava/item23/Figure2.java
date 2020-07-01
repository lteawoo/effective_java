package kr.taeu.effectiveJava.item23;

/*
 * 태그 달린 클래스를 클래스 계층구조로 변환
 */
abstract class Figure2 {
  abstract double area();
}

class Circle extends Figure2 {
  final double radius;
  
  Circle(double radius) {
    this.radius = radius;
  }
  
  @Override
  double area() {
    return Math.PI * (radius * radius);
  }
}

class Rectangle extends Figure2 {
  final double length;
  final double width;
  
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }
  
  @Override
  double area() {
    return length * width;
  }
}
