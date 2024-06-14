import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchTerm: string): any[] {
    if (!items) return [];
    if (!searchTerm) return items.slice(0, 10);
  
    searchTerm = searchTerm.toLowerCase().trim();

    if (searchTerm.length === 0) {
      return items.slice(0, 10);
    }
  
    return items.filter(it => {
      return (it.email ? it.email.toLowerCase().includes(searchTerm) : false) || 
             (it.alias ? it.alias.toLowerCase().includes(searchTerm) : false);
    });
  }
}