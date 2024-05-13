import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {
  totalNewMessageCount: number = 0;
  constructor() {
  }
}