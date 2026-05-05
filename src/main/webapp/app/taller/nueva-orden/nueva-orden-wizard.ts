import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { VehiculoService } from 'app/entities/vehiculo/service/vehiculo.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { EstadoOrden } from 'app/entities/enumerations/estado-orden.model';

@Component({
  selector: 'jhi-nueva-orden-wizard',
  templateUrl: './nueva-orden-wizard.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, FontAwesomeModule, CommonModule],
})
export class NuevaOrdenWizard implements OnInit {
  paso = signal(1);
  guardando = signal(false);
  errorMsg = signal<string | null>(null);

  // ── Paso 1: búsqueda de vehículo ──
  busquedaVehiculo = '';
  readonly vehiculosSugeridos = signal<IVehiculo[]>([]);
  vehiculoSeleccionado: IVehiculo | null = null;
  clienteDelVehiculo: ICliente | null = null;

  // Formulario nuevo cliente + vehículo
  mostrarFormNuevo = false;
  guardandoNuevo = false;
  nuevoCliente = { nombre: '', cedula: '', telefono: '', email: '', direccion: '' };
  nuevoVehiculo = { placa: '', marca: '', modelo: '', color: '' };

  // ── Paso 1: datos de la orden ──
  paso1 = {
    kilometraje: '',
    mecanico: '',
    estado: EstadoOrden.PENDIENTE as keyof typeof EstadoOrden,
    observaciones: '',
    ingresoGrua: false,
  };

  // ── Paso 2 ──
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
  paso2 = {
    trabajoEnderezado: [] as string[],
    trabajoPintura: [] as string[],
    inventario: [] as string[],
    nivelCombustible: 0,
  };

  // ── Paso 3 ──
  paso3 = { valorPactado: '', abono: '', saldo: '', trabajosExtras: '' };

  readonly estadoOrdenValues = Object.keys(EstadoOrden);

  private router = inject(Router);
  private http = inject(HttpClient);
  private appConfig = inject(ApplicationConfigService);
  private clienteService = inject(ClienteService);
  private vehiculoService = inject(VehiculoService);
  private vehiculoSearch$ = new Subject<string>();

  ngOnInit(): void {
    const col = Math.ceil(this.partes.length / 3);
    this.partesCol1 = this.partes.slice(0, col);
    this.partesCol2 = this.partes.slice(col, col * 2);
    this.partesCol3 = this.partes.slice(col * 2);
    const inv = Math.ceil(this.inventarioItems.length / 2);
    this.inventarioCol1 = this.inventarioItems.slice(0, inv);
    this.inventarioCol2 = this.inventarioItems.slice(inv);

    this.vehiculoSearch$
      .pipe(
        debounceTime(150),
        switchMap((q: string) => this.vehiculoService.search(q)),
      )
      .subscribe((r: IVehiculo[]) => {
        this.vehiculosSugeridos.set(r);
      });
  }

  // ── Búsqueda vehículo ──
  onBuscarVehiculo(): void {
    if (this.vehiculoSeleccionado) return;
    const q = this.busquedaVehiculo.trim();
    if (q.length >= 1) {
      this.vehiculoSearch$.next(q);
    } else {
      this.vehiculosSugeridos.set([]);
    }
  }

  seleccionarVehiculo(v: IVehiculo): void {
    this.vehiculoSeleccionado = v;
    this.clienteDelVehiculo = v.cliente ?? null;
    this.busquedaVehiculo = `${v.placa} — ${v.marca} ${v.modelo}`;
    this.vehiculosSugeridos.set([]);
    this.mostrarFormNuevo = false;
  }

  limpiarVehiculo(): void {
    this.vehiculoSeleccionado = null;
    this.clienteDelVehiculo = null;
    this.busquedaVehiculo = '';
    this.vehiculosSugeridos.set([]);
  }

  // ── Formulario nuevo cliente + vehículo ──
  abrirFormNuevo(): void {
    this.mostrarFormNuevo = true;
    this.nuevoVehiculo.placa = this.busquedaVehiculo;
    this.vehiculosSugeridos.set([]);
  }

  cancelarFormNuevo(): void {
    this.mostrarFormNuevo = false;
  }

  guardarNuevoClienteYVehiculo(): void {
    if (!this.nuevoCliente.nombre.trim() || !this.nuevoVehiculo.placa.trim()) return;
    this.guardandoNuevo = true;
    this.errorMsg.set(null);

    // 1. Crear cliente
    this.clienteService.create(this.nuevoCliente as any).subscribe({
      next: cliente => {
        // 2. Crear vehículo asociado al cliente
        const vBody: any = { ...this.nuevoVehiculo, cliente: { id: cliente.id } };
        this.vehiculoService.create(vBody).subscribe({
          next: vehiculo => {
            this.vehiculoSeleccionado = { ...vehiculo, cliente };
            this.clienteDelVehiculo = cliente;
            this.busquedaVehiculo = `${vehiculo.placa} — ${vehiculo.marca} ${vehiculo.modelo}`;
            this.mostrarFormNuevo = false;
            this.guardandoNuevo = false;
            this.paso.update(p => p + 1);
          },
          error: () => {
            this.guardandoNuevo = false;
            this.errorMsg.set('Error al crear el vehículo.');
          },
        });
      },
      error: () => {
        this.guardandoNuevo = false;
        this.errorMsg.set('Error al crear el cliente.');
      },
    });
  }

  // ── Paso 2 ──
  isEnderezado(p: string): boolean {
    return this.paso2.trabajoEnderezado.includes(p);
  }
  isPintura(p: string): boolean {
    return this.paso2.trabajoPintura.includes(p);
  }
  isInventario(i: string): boolean {
    return this.paso2.inventario.includes(i);
  }
  toggleEnderezado(p: string): void {
    this.toggle(this.paso2.trabajoEnderezado, p);
  }
  togglePintura(p: string): void {
    this.toggle(this.paso2.trabajoPintura, p);
  }
  toggleInventario(i: string): void {
    this.toggle(this.paso2.inventario, i);
  }
  private toggle(arr: string[], v: string): void {
    const idx = arr.indexOf(v);
    if (idx >= 0) arr.splice(idx, 1);
    else arr.push(v);
  }
  get combustibleSegs(): number[] {
    return Array.from({ length: 8 }, (_, i) => i);
  }
  setCombustible(n: number): void {
    this.paso2.nivelCombustible = n + 1;
  }

  // ── Paso 3 ──
  calcularSaldo(): void {
    const vp = parseFloat(this.paso3.valorPactado) || 0;
    const ab = parseFloat(this.paso3.abono) || 0;
    this.paso3.saldo = (vp - ab).toFixed(2);
  }

  // ── Navegación ──
  siguiente(): void {
    if (this.paso() === 1 && !this.vehiculoSeleccionado) {
      this.errorMsg.set('Debe seleccionar o registrar un vehículo para continuar.');
      return;
    }
    this.errorMsg.set(null);
    this.paso.update(p => p + 1);
  }

  anterior(): void {
    this.paso.update(p => p - 1);
  }

  guardar(generarPdf = false): void {
    this.guardando.set(true);
    this.errorMsg.set(null);
    const ordenUrl = this.appConfig.getEndpointFor('api/orden-trabajos');
    const body = {
      fecha: new Date().toISOString().split('T')[0],
      estado: this.paso1.estado,
      observaciones: this.paso1.observaciones,
      mecanico: this.paso1.mecanico,
      valorPactado: this.paso3.valorPactado ? parseFloat(this.paso3.valorPactado) : null,
      abono: this.paso3.abono ? parseFloat(this.paso3.abono) : null,
      saldo: this.paso3.saldo ? parseFloat(this.paso3.saldo) : null,
      trabajosExtras: this.paso3.trabajosExtras || null,
      vehiculo: this.vehiculoSeleccionado ? { id: this.vehiculoSeleccionado.id } : null,
    };
    this.http.post<any>(ordenUrl, body).subscribe({
      next: orden => {
        if (generarPdf) this.generarPdfOrden(orden.id);
        else {
          this.guardando.set(false);
          this.router.navigate(['/orden-trabajo']);
        }
      },
      error: () => {
        this.guardando.set(false);
        this.errorMsg.set('Error al guardar la orden.');
      },
    });
  }

  private generarPdfOrden(ordenId: number): void {
    const pdfUrl = this.appConfig.getEndpointFor(`api/orden-trabajos/${ordenId}/pdf`);
    const pdfWindow = window.open('', '_blank');
    if (pdfWindow)
      pdfWindow.document.write(
        '<html><body style="margin:0;background:#525659;display:flex;justify-content:center;align-items:center;height:100vh"><p style="color:#fff;font-family:Arial;font-size:18px">Generando PDF...</p></body></html>',
      );
    const pdfBody = {
      trabajoEnderezado: this.paso2.trabajoEnderezado,
      trabajoPintura: this.paso2.trabajoPintura,
      inventario: this.paso2.inventario,
      nivelCombustible: this.paso2.nivelCombustible,
      kilometraje: this.paso1.kilometraje,
      ingresoGrua: this.paso1.ingresoGrua,
      valorPactado: this.paso3.valorPactado,
      abono: this.paso3.abono,
      saldo: this.paso3.saldo,
      trabajosExtras: this.paso3.trabajosExtras,
    };
    this.http.post(pdfUrl, pdfBody, { responseType: 'blob' }).subscribe({
      next: blob => {
        const fileUrl = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
        if (pdfWindow) pdfWindow.location.href = fileUrl;
        this.guardando.set(false);
        this.router.navigate(['/orden-trabajo']);
      },
      error: () => {
        if (pdfWindow) pdfWindow.close();
        this.guardando.set(false);
        this.router.navigate(['/orden-trabajo']);
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/orden-trabajo']);
  }
}
