import { Component, inject, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IOrdenTrabajo } from '../orden-trabajo.model';

export interface PdfFormData {
  trabajoEnderezado: string[];
  trabajoPintura: string[];
  inventario: string[];
  nivelCombustible: number;
  kilometraje: string;
  ingresoGrua: boolean;
  valorPactado: string;
  abono: string;
  saldo: string;
  trabajosExtras: string;
}

@Component({
  selector: 'jhi-orden-trabajo-pdf-dialog',
  templateUrl: './orden-trabajo-pdf-dialog.html',
  imports: [FormsModule],
})
export class OrdenTrabajoPdfDialog implements OnInit {
  @Input() ordenId!: number;
  @Input() orden?: IOrdenTrabajo;

  readonly partes = [
    'Guardachoque D.',
    'Guardachoque T.',
    'Capó',
    'Techo',
    'Guardafango DR',
    'Guardafango DL',
    'Guardafango FR',
    'Guardafango FL',
    'Compuerta',
    'Estribo F',
    'Estribo L',
    'Puerta DR',
    'Puerta DL',
    'Puerta TR',
    'Puerta TL',
    'Espejo R',
    'Espejo L',
    'Piso',
    'Pintura Integral',
  ];

  readonly inventarioItems = [
    'Gato',
    'Herramientas',
    'Triángulos',
    'Tapetes',
    'Llanta refacción',
    'Extintor',
    'Antena',
    'Emblemas',
    'Tapones de rueda',
    'Cables',
    'Estéreo',
    'Encendedor',
  ];

  partesCol1: string[] = [];
  partesCol2: string[] = [];
  partesCol3: string[] = [];
  inventarioCol1: string[] = [];
  inventarioCol2: string[] = [];

  form: PdfFormData = {
    trabajoEnderezado: [],
    trabajoPintura: [],
    inventario: [],
    nivelCombustible: 0,
    kilometraje: '',
    ingresoGrua: false,
    valorPactado: '',
    abono: '',
    saldo: '',
    trabajosExtras: '',
  };

  generando = false;

  protected activeModal = inject(NgbActiveModal);
  private http = inject(HttpClient);
  private appConfig = inject(ApplicationConfigService);

  ngOnInit(): void {
    const col = Math.ceil(this.partes.length / 3);
    this.partesCol1 = this.partes.slice(0, col);
    this.partesCol2 = this.partes.slice(col, col * 2);
    this.partesCol3 = this.partes.slice(col * 2);

    const inv = Math.ceil(this.inventarioItems.length / 2);
    this.inventarioCol1 = this.inventarioItems.slice(0, inv);
    this.inventarioCol2 = this.inventarioItems.slice(inv);

    // Pre-cargar valores guardados en la BD
    if (this.orden) {
      if (this.orden.valorPactado != null) this.form.valorPactado = String(this.orden.valorPactado);
      if (this.orden.abono != null) this.form.abono = String(this.orden.abono);
      if (this.orden.trabajosExtras) this.form.trabajosExtras = this.orden.trabajosExtras;
      this.calcularSaldo();
    }
  }

  isEnderezado(parte: string): boolean {
    return this.form.trabajoEnderezado.includes(parte);
  }
  isPintura(parte: string): boolean {
    return this.form.trabajoPintura.includes(parte);
  }
  isInventario(item: string): boolean {
    return this.form.inventario.includes(item);
  }

  toggleEnderezado(parte: string): void {
    this.toggle(this.form.trabajoEnderezado, parte);
  }
  togglePintura(parte: string): void {
    this.toggle(this.form.trabajoPintura, parte);
  }
  toggleInventario(item: string): void {
    this.toggle(this.form.inventario, item);
  }

  private toggle(arr: string[], val: string): void {
    const idx = arr.indexOf(val);
    if (idx >= 0) arr.splice(idx, 1);
    else arr.push(val);
  }

  get combustibleSegmentos(): number[] {
    return Array.from({ length: 8 }, (_, i) => i);
  }

  setCombustible(nivel: number): void {
    this.form.nivelCombustible = nivel + 1;
  }

  calcularSaldo(): void {
    const pactado = parseFloat(this.form.valorPactado) || 0;
    const abono = parseFloat(this.form.abono) || 0;
    this.form.saldo = (pactado - abono).toFixed(2);
  }

  cancel(): void {
    this.activeModal.dismiss();
  }

  generarPdf(): void {
    this.generando = true;

    // Abrir la ventana ANTES de la llamada async para evitar el popup blocker
    const pdfWindow = window.open('', '_blank');
    if (pdfWindow) {
      pdfWindow.document.write(
        '<html><body style="margin:0;background:#525659;display:flex;justify-content:center;align-items:center;height:100vh"><p style="color:#fff;font-family:Arial;font-size:18px">Generando PDF...</p></body></html>',
      );
    }

    const url = this.appConfig.getEndpointFor(`api/orden-trabajos/${this.ordenId}/pdf`);
    this.http.post(url, this.form, { responseType: 'blob' }).subscribe({
      next: blob => {
        const fileUrl = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
        if (pdfWindow) {
          pdfWindow.location.href = fileUrl;
        } else {
          // Fallback si el popup fue bloqueado
          const a = document.createElement('a');
          a.href = fileUrl;
          a.target = '_blank';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
        }
        this.generando = false;
        this.activeModal.close();
      },
      error: () => {
        if (pdfWindow) pdfWindow.close();
        this.generando = false;
      },
    });
  }
}
