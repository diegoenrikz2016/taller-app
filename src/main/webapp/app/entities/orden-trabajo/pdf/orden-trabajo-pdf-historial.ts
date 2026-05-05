import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

export interface PdfMeta {
  id: number;
  nombreArchivo: string;
  fechaGeneracion: string;
  ordenTrabajoId: number;
}

@Component({
  selector: 'jhi-orden-trabajo-pdf-historial',
  templateUrl: './orden-trabajo-pdf-historial.html',
  imports: [RouterLink, DatePipe, FontAwesomeModule],
})
export class OrdenTrabajoPdfHistorial implements OnInit {
  readonly pdfs = signal<PdfMeta[]>([]);
  readonly isLoading = signal(true);
  readonly errorMsg = signal<string | null>(null);

  private readonly http = inject(HttpClient);
  private readonly appConfig = inject(ApplicationConfigService);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.isLoading.set(true);
    this.errorMsg.set(null);
    const url = this.appConfig.getEndpointFor('api/orden-trabajos/pdfs');
    this.http.get<PdfMeta[]>(url).subscribe({
      next: data => {
        this.pdfs.set(Array.isArray(data) ? data : []);
        this.isLoading.set(false);
      },
      error: err => {
        this.errorMsg.set(`Error ${err.status}: ${err.message}`);
        this.isLoading.set(false);
      },
    });
  }

  descargar(pdf: PdfMeta): void {
    const url = this.appConfig.getEndpointFor(`api/orden-trabajos/pdfs/${pdf.id}/download`);
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: blob => {
        const a = document.createElement('a');
        a.href = window.URL.createObjectURL(blob);
        a.download = pdf.nombreArchivo;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      },
      error: err => this.errorMsg.set(`Error al descargar: ${err.status}`),
    });
  }
}
