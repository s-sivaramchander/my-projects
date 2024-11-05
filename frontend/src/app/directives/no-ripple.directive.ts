import { Directive, ElementRef, OnInit } from '@angular/core';
import { MatButton } from '@angular/material/button';

@Directive({
  selector: '[noRipple]'
})
export class NoRippleDirective implements OnInit {
  constructor(
    private button: MatButton,
    private elementRef: ElementRef
  ) {}

  ngOnInit() {
    this.button.disableRipple = true;
    // Also remove ripple element if it exists
    const rippleElement = this.elementRef.nativeElement.querySelector('.mat-ripple');
    if (rippleElement) {
      rippleElement.remove();
    }
  }
}