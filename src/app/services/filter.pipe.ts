import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchTerm: string): any[] {
    if (!items) return [];
    if (!searchTerm) return items;
  
    searchTerm = searchTerm.toLowerCase().trim();
  
    return items.filter(it => {
      return (it.email ? it.email.toLowerCase().includes(searchTerm) : false) || 
             (it.alias ? it.alias.toLowerCase().includes(searchTerm) : false);
    });
  }
}